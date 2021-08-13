package com.javan.showdocutil.docs.showdoc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.javan.showdocutil.data.ControllerClazzAggre;
import com.javan.showdocutil.data.MethodDomain;
import com.javan.showdocutil.data.RequestDomain;
import com.javan.showdocutil.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.typeManufacturers.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Desc showdoc工具类
 * @Author Javan Feng
 * @Date 2019 06 2019/6/23 0:32
 */
public class ControllerShowDocUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerShowDocUtils.class);

    /**
     * loader
     */
    public static final ClassLoader CLASS_LOAD = ClassUtils.getDefaultClassLoader();
    public static final int MOCK_COLLECTION_NUMBER_OF_COLLECTION_ELEMENTS = 1;
    private static CustomCollectionTypeManufacturerImpl collectionTypeManufacturer;
    /**
     * 模拟数据
     */
    private static Map<Class, TypeManufacturer> SELF_DEFINE_EXAMPLE = new HashMap<>();

    public static <T> void addInitExampleValue(Class<? extends T> type, TypeManufacturer<T> typeManufacturer) {
        SELF_DEFINE_EXAMPLE.put(type, typeManufacturer);
    }

    // get content of text
    public static List<ShowDocModel> getText(String packagePath, ShowDocConfiguration baseConfiguration) throws Exception {
        List<ControllerClazzAggre> controllerClazzAggreByPath = ControllerParseUtils.getControllerClazzAggreByPath(packagePath);
        //
        List<ShowDocModel> showDocLists = new ArrayList<>();
        for (ControllerClazzAggre className : controllerClazzAggreByPath) {
            List<ShowDocModel> showDocModels = buildShowDocModel(className, baseConfiguration);
            showDocLists.addAll(showDocModels);
        }

        return showDocLists;
    }

    // get content of text
    public static List<ShowDocModel> getText(Class<?> clazz, ShowDocConfiguration baseConfiguration) throws Exception {
        return buildShowDocModel(ControllerParseUtils.getControllerClazzAggre(clazz), baseConfiguration);
    }

    // get content of text
    public static ShowDocModel getText(Class<?> aClass, String simpleMethodName, ShowDocConfiguration baseConfiguration) throws Exception {
        return buildShowDocModel(ControllerParseUtils.getControllerClazzAggre(aClass, simpleMethodName), baseConfiguration).get(0);
    }

    private static List<ShowDocModel> buildShowDocModel(ControllerClazzAggre className, ShowDocConfiguration baseConfiguration) {
        initCustomMockDatas(baseConfiguration);
        final List<MethodDomain> methodList = className.getMethodList();
        List<ShowDocModel> showDocModelList = new ArrayList<>(methodList.size());
        for (MethodDomain methodDomain : methodList) {
            // folder
            String folder = JavaDocCommonUtils.getFolder(className);
            // title-
            String title = folder + "-" + JavaDocCommonUtils.getTitle(methodDomain);
            // description
            final String desc = CustomTagForReqNameUtils.getMethodDesc(methodDomain.getTags());
            // method
            final RequestDomain request = methodDomain.getRequest();
            String requestMethod = request.getRequestMethod();
            //  request param
            final List<ShowReqRespModel> requestParam = JavaDocCommonUtils.getRequestParam(methodDomain);
            JavaDocCommonUtils.simplifyType(requestParam);
            // return model info
            final List<List<ShowReqRespModel>> returnParams = JavaDocCommonUtils.getReturn(methodDomain,baseConfiguration);
            final List<ShowReqRespModel> returnParam = returnParams.get(0);
            final List<ShowReqRespModel> commonParam = returnParams.get(1);
            JavaDocCommonUtils.simplifyType(returnParam);
            JavaDocCommonUtils.simplifyType(commonParam);
            // example ,often a postman result,template:
            PodamFactory podamFactory = initPodamFactory();
            // todo 默认先实现单个为json吧，多
            final Method method = methodDomain.getMethod();
            String reqExample = buildReqParamExample(requestMethod, podamFactory, method);
            final String respExample = doBuildReturnExample(podamFactory, method);
            ContentBuilder contentBuilder = ContentBuilder.newBuild()
                    .withRequestMethod(requestMethod)
                    .withRequestParam(requestParam)
                    .withRequestReturn(returnParam)
                    .withRequestReturnCommon(commonParam)
                    .withBusinessName(getBusinessNameBy(commonParam))
                    .withRespExample(respExample)
                    .withTitle(title)
                    .withDescription(desc)
                    .withRequestDomain(request)
                    .withConfiguration(baseConfiguration)
                    .withUrlList(request.getUrlList().toArray(new String[request.getUrlList().size()]))
                    .withReqExample(reqExample)
                    .withRespExample(respExample);

            final String content = TemplateUtils.parse2Str(contentBuilder, baseConfiguration.getTemplateLocation());
            ShowDocModel showDocModel = new ShowDocModel();
            showDocModel.setTitle(title);
            showDocModel.setFolder(folder);
            showDocModel.setContent(content);
            showDocModelList.add(showDocModel);
        }
        return showDocModelList;
    }

    private static String getBusinessNameBy(List<ShowReqRespModel> commonParam) {
        if(commonParam == null || commonParam.isEmpty()){
            return null;
        }

        final ShowReqRespModel showReqRespModel = commonParam.get(commonParam.size() - 1);
        return JavaDocCommonUtils.delHtmlTag(showReqRespModel.getName());
    }

    private static void initCustomMockDatas(ShowDocConfiguration baseConfiguration) {
        final String mockJsonFileLocation = baseConfiguration.getMockJsonFileLocation();
        if (mockJsonFileLocation == null || mockJsonFileLocation.trim().length() == 0) {
            return;
        }

        doBuildMockDataFromFile(mockJsonFileLocation);
    }

    private static void doAddLongMock(List<Map<String, Object>> stringObjectMap) {
        final Map<String, Long> longMap = filterMockByClass(stringObjectMap, Long.class);
        addInitExampleValue(Long.class, new LongTypeManufacturerImpl() {
            @Override
            public Long getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
                String key = attributeMetadata.getAttributeName();
                if (longMap.containsKey(key)) {
                    return longMap.get(key);
                }
                return super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            }
        });
    }


    private static void doAddCollectionMock(List<Map<String, Object>> stringObjectMap) {
        final Map<String, List<Object>> collectionMap = stringObjectMap.stream().flatMap(e -> e.entrySet().stream())
                .filter(e -> e.getValue() != null)
                .filter(e -> e.getValue().getClass().isAssignableFrom(JSONArray.class))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> ((JSONArray) e.getValue()).toJavaList(Object.class),(e1,e2)->e2));

        collectionTypeManufacturer = new CustomCollectionTypeManufacturerImpl() {
            @SuppressWarnings("unchecked")
            @Override
            public Collection getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
                String key = attributeMetadata.getAttributeName();
                if (collectionMap.containsKey(key)) {
                    final List<Object> objects = collectionMap.get(key);
                    if (objects != null && !objects.isEmpty()) {
                        if (objects.size() > MOCK_COLLECTION_NUMBER_OF_COLLECTION_ELEMENTS) {
                            return objects.subList(0, MOCK_COLLECTION_NUMBER_OF_COLLECTION_ELEMENTS);
                        } else {
                            return objects;
                        }
                    }
                }
                return super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            }
        };
    }

    private static void doAddDoubleMock(List<Map<String, Object>> stringObjectMap) {
        final Map<String, Double> doubleMap = filterMockByClass(stringObjectMap, Double.class);
        addInitExampleValue(Double.class, new DoubleTypeManufacturerImpl() {
            @Override
            public Double getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
                String key = attributeMetadata.getAttributeName();
                if (doubleMap.containsKey(key)) {
                    return doubleMap.get(key);
                }
                return super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            }
        });
    }

    private static void doAddIntMock(List<Map<String, Object>> stringObjectMap) {
        final Map<String, Integer> stringMap = filterMockByClass(stringObjectMap, Integer.class);
        addInitExampleValue(Integer.class, new IntTypeManufacturerImpl() {
            @Override
            public Integer getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
                String key = attributeMetadata.getAttributeName();
                if (stringMap.containsKey(key)) {
                    return stringMap.get(key);
                }
                return super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            }
        });
    }

    private static <T> Map<String, T> filterMockByClass(List<Map<String, Object>> stringObjectMap, Class<T> clazz) {
        return stringObjectMap.stream().flatMap(e -> e.entrySet().stream())
                .filter(e -> e.getValue() != null)
                .filter(e -> e.getValue().getClass().isAssignableFrom(clazz))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> ((T) e.getValue()),(e1,e2)->e2));
    }

    private static void doAddStringMock(List<Map<String, Object>> stringObjectMap) {
        final Map<String, String> stringMap = filterMockByClass(stringObjectMap, String.class);
        addInitExampleValue(String.class, new StringTypeManufacturerImpl() {
            @Override
            public String getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
                String key = attributeMetadata.getAttributeName();
                if (stringMap.containsKey(key)) {
                    return stringMap.get(key);
                }
                return super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            }
        });
    }

    private static void doBuildMockDataFromFile(String mockJsonFileLocation) {
        ClassPathResource resource = new ClassPathResource(mockJsonFileLocation);
        if (resource.exists()) {
            try {
                try (InputStream inputStream = resource.getInputStream();
                     BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    final String[] jsonModelArray = sb.toString().split(";");
                    // 结果map
                    final List<Map<String, Object>> mapList = Arrays.stream(jsonModelArray)
                            .map(e -> {
                                try {
                                    Map<String, Object> all = new HashMap<>();
                                    final Map<String, Object> innerMap = JSON.parseObject(e).getInnerMap();
                                    buildAllJsonMap(innerMap,all);
                                    return all;
                                } catch (Exception l) {
                                    // ignore
                                    return new HashMap<String, Object>();
                                }
                            }).collect(Collectors.toList());
                    // 只添加常见的 string int long double list
                    doAddStringMock(mapList);
                    doAddIntMock(mapList);
                    doAddDoubleMock(mapList);
                    doAddCollectionMock(mapList);
                    doAddLongMock(mapList);
                }
            } catch (Exception e) {
                //
                LOGGER.error("",e);
            }
        }
    }


    private static void buildAllJsonMap(Map<String, Object> innerMap, Map<String, Object> all) {
        innerMap.forEach((k, v) -> {
            if (v instanceof JSONObject) {
                if (!((JSONObject) v).isEmpty()) {
                    final Map<String, Object> childMap = ((JSONObject) v).getInnerMap();
                    buildAllJsonMap(childMap,all);
                }
            } else {
                all.put(k, v);
            }
        });
    }

    private static String doBuildReturnExample(PodamFactory podamFactory, Method method) {
        final Type returnType = method.getGenericReturnType();
        Type[] types;
        if (returnType instanceof ParameterizedTypeImpl) {
            types = ((ParameterizedTypeImpl) returnType).getActualTypeArguments();
        } else {
            types = new Type[]{String.class};
        }
        Object bean = podamFactory.manufacturePojo(method.getReturnType(), types);
        final String s = JSON.toJSONString(bean, SerializerFeature.WriteMapNullValue,SerializerFeature.PrettyFormat);
        return bean == null ? "" : s;
    }

    private static String buildReqParamExample(String requestMethod, PodamFactory podamFactory, Method method) {
        StringBuilder reqJson = new StringBuilder();
        final Parameter[] parameters = method.getParameters();
        if (parameters != null && parameters.length != 0) {
            for (Parameter parameter : parameters) {
                final Class<?> type = parameter.getType();
                final Type parameterizedType = parameter.getParameterizedType();
                Type[] types;
                if (parameterizedType instanceof ParameterizedTypeImpl) {
                    types = ((ParameterizedTypeImpl) parameterizedType).getActualTypeArguments();
                } else {
                    types = new Type[]{String.class};
                }
                Object bean = podamFactory.manufacturePojo(type, types);
                if (type.isPrimitive() || BaseTypeUtil.isBaseType(type.getTypeName())) {
                    if (!requestMethod.contains("POST")) {
                        reqJson.append(parameter.getName());
                        reqJson.append(" : ");
                        reqJson.append(bean);
                    } else {
                        reqJson.append("{");
                        reqJson.append(System.lineSeparator());
                        reqJson.append(parameter.getName());
                        reqJson.append(" : ");
                        reqJson.append(bean);
                        reqJson.append(System.lineSeparator());
                        reqJson.append("}");
                    }
                } else {
                    if (!requestMethod.contains("POST")) {
                        reqJson.append(parameter.getName());
                        reqJson.append(" : ");
                        reqJson.append(JSON.toJSONString(bean, SerializerFeature.WriteMapNullValue,SerializerFeature.PrettyFormat));
                        reqJson.append(System.lineSeparator());
                    } else {
                        reqJson.append(JSON.toJSONString(bean, SerializerFeature.WriteMapNullValue,SerializerFeature.PrettyFormat));
                    }
                }
                reqJson.append(",");
                reqJson.append(System.lineSeparator());
            }
        }
        if (reqJson.length() != 0) {
            return reqJson.substring(0, reqJson.length() - (System.lineSeparator().length() + 1));

        }
        return reqJson.toString();
    }

    private static PodamFactory initPodamFactory() {
        PodamFactory factory = new PodamFactoryImpl();
        SELF_DEFINE_EXAMPLE.forEach((key, value) -> factory.getStrategy().addOrReplaceTypeManufacturer(key, value));
        if (collectionTypeManufacturer != null) {
            factory.getStrategy().addOrReplaceTypeManufacturer(Collection.class, collectionTypeManufacturer);
        }
        factory.getStrategy().setDefaultNumberOfCollectionElements(MOCK_COLLECTION_NUMBER_OF_COLLECTION_ELEMENTS);
        return factory;
    }
}
