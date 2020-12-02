package com.javan.showdocutil.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.javan.showdocutil.model.ControllerClassInfo;
import com.javan.showdocutil.model.MethodInfo;
import com.javan.showdocutil.model.MethodParamInfo;
import com.javan.showdocutil.model.MethodReturnInfo;
import com.javan.showdocutil.model.build.MethodParamInfoBuilder;
import com.javan.showdocutil.model.build.MethodReturnInfoBuilder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @Desc showdoc工具类
 * @Author Javan Feng
 * @Date 2019 06 2019/6/23 0:32
 */
class ControllerShowDocUtils {

    private static final Logger LOGGER = java.util.logging.Logger.getLogger("ControllerShowDocUtils");
    /**
     * 扫描@Controller类
     */
    private static final ControllerClassPathScanner SCANNNER = new ControllerClassPathScanner();

    //
    private static final Set<String> HAD_PARSED_CLASS_MAP = new HashSet<>();

    /**
     * loader
     */
    private static final ClassLoader CLASS_LOAD = ClassUtils.getDefaultClassLoader();

    // get content of text
    public static List<ShowDocModel> getText(String packagePath) throws Exception {
        Set<BeanDefinition> candidateComponents = SCANNNER.findCandidateComponents(packagePath);
        // className
        List<String> classNames = candidateComponents.stream().map(BeanDefinition::getBeanClassName).collect(Collectors.toList());
        //
        List<ShowDocModel> showDocLists = new ArrayList<>();
        for (String className : classNames) {
            List<ShowDocModel> showDocModels = parseComment(className);
            showDocLists.addAll(showDocModels);
        }

        return showDocLists;
    }

    // get content of text
    public static List<ShowDocModel> getText(Class<?> clazz) throws Exception {
        List<ShowDocModel> showDocModels = parseComment(clazz.getTypeName());
        return showDocModels;
    }

    // get content of text
    public static ShowDocModel getText(Class<?> aClass, String simpleMethodName) throws Exception {
        // clazz
        System.out.println("正在读取Controller类信息....");
        if (isMatch(aClass)) {
            // 前缀
            String[] requestUriPrefix = getRequestUriPrefix(aClass);
            // method 信息
            List<MethodInfo> methods = getRequestMethodAndUriSuffix(aClass, requestUriPrefix);
            // 返回showdoc 所需信息
            Optional<MethodInfo> first = methods.stream().filter(methodInfo -> methodInfo.getMethod().getName().equals(simpleMethodName)).findFirst();
            if (!first.isPresent()) {
                LOGGER.info(() -> "not found the methd[{" + simpleMethodName + "}] in class [{" + aClass.getName() + "}]");
                return null;
            } else {
                List<ShowDocModel> showDocModels = buildShowDocModelList(new ControllerClassInfo(Collections.singletonList(first.get()), aClass));
                if (!showDocModels.isEmpty()) {
                    return showDocModels.get(0);
                }
            }
        }

        return null;
    }

    // parse the content
    private static List<ShowDocModel> parseComment(String className) throws Exception {
        // clazz
        System.out.println("正在读取Controller类信息....");
        Class<?> aClass = CLASS_LOAD.loadClass(className);
        if (isMatch(aClass)) {
            // 前缀
            String[] requestUriPrefix = getRequestUriPrefix(aClass);
            // method 信息
            List<MethodInfo> method = getRequestMethodAndUriSuffix(aClass, requestUriPrefix);
            // 返回showdoc 所需信息
            return buildShowDocModelList(new ControllerClassInfo(method, aClass));
        }

        return Collections.emptyList();
    }

    // model
    private static List<ShowDocModel> buildShowDocModelList(ControllerClassInfo classInfo) throws Exception {
        List<MethodInfo> method = classInfo.getList();
        if (method == null || method.isEmpty()) {
            return Collections.emptyList();
        }

        System.out.println("开始进行Doclet解析.....");
        parseShowDoc(classInfo);

        List<ShowDocModel> list = new ArrayList<>();
        for (MethodInfo methodInfo : method) {
            list.add(getShowDocModel(methodInfo));
        }
        return list;
    }

  /*  // model
    private static List<ShowDocModel> buildShowDocModelList(List<MethodInfo> method) throws Exception {
        if (method == null || method.isEmpty()) {
            return Collections.emptyList();
        }

        List<ShowDocModel> list = new ArrayList<>();
        for (MethodInfo methodInfo : method) {
            list.add(parseShowDoc(methodInfo));
        }

        return list;
    }*/


    // method - suffix array
    private static List<MethodInfo> getRequestMethodAndUriSuffix(Class<?> aClass, String[] requestUriPrefix) {
        Method[] declaredMethods = aClass.getDeclaredMethods();
        List<MethodInfo> list = new ArrayList<>(declaredMethods.length);
        for (Method method : declaredMethods) {
            RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
            if (requestMapping != null) {
                MethodInfo info = new MethodInfo();
                info.setUriPrefix(requestUriPrefix);
                RequestMethod[] methods = requestMapping.method();

                // uri suffix and method
                if (methods.length == 0) {
                    // is requestMapping default get and post
                    info.setRequestMethod("GET,POST");
                    info.setUrisSuffix(requestMapping.value());
                } else {
                    String requestMethod = Arrays.stream(methods).map(RequestMethod::name).collect(Collectors.joining(","));
                    info.setRequestMethod(requestMethod);
                    String[] uriSuffixArray = getRequestMappingUri(method, requestMapping);
                    info.setUrisSuffix(uriSuffixArray);
                }

                // method
                info.setMethod(method);
                List<MethodParamInfo> methodParamInfos = MethodParamInfoBuilder.build(method);
                info.setParams(methodParamInfos);
                MethodReturnInfo returnInfo = MethodReturnInfoBuilder.build(method);
                info.setReturnInfo(returnInfo);
                // class
                info.setSourceClass(aClass);
                list.add(info);

            }
        }
        return list;
    }


    private static String[] getRequestUriPrefix(Class<?> aClass) {
        RequestMapping annotation = aClass.getAnnotation(RequestMapping.class);
        String[] requestPrefix = null;
        if (annotation != null) {
            // path prefix
            requestPrefix = annotation.value();
        }
        return requestPrefix;
    }


    // match
    private static boolean isMatch(Class<?> clazz) {
        Controller annotation = AnnotationUtils.findAnnotation(clazz, Controller.class);
        if (annotation == null) {
            // ignore
            LOGGER.info("ignore the class " + clazz + " cause not contains @Controller or @RestController");
            return false;
        }

        return true;
    }


    // get suffix of uri
    private static String[] getRequestMappingUri(Method method, RequestMapping requestMapping) {
        // make sure is requestMapping or getMapping or PostMapping

        String[] value = requestMapping.value();
        if (value != null || value.length == 0) {
            // may be otherMapping
            return OtherMappingValues(method, requestMapping);
        }
        // is requestMapping
        return value;
    }

    // otherMapping value
    private static String[] OtherMappingValues(Method method, RequestMapping requestMapping) {
        RequestMethod[] requestMethods = requestMapping.method();
        // must has only one method
        RequestMethod requestMethod = requestMethods[0];
        switch (requestMethod) {
            case GET:
                GetMapping getMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
                return getAnnotationRequestMappingVale(getMapping);
            case POST:
                PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
                return getAnnotationRequestMappingVale(postMapping);
            case PUT:
                PutMapping putMapping = AnnotationUtils.findAnnotation(method, PutMapping.class);
                return getAnnotationRequestMappingVale(putMapping);
            case PATCH:
                PatchMapping patchMapping = AnnotationUtils.findAnnotation(method, PatchMapping.class);
                return getAnnotationRequestMappingVale(patchMapping);
            case DELETE:
                DeleteMapping annotation = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
                return getAnnotationRequestMappingVale(annotation);
            default:
                LOGGER.info("not support method,may met a problem with this program");
                return new String[0];

        }
    }

    // requestMapping value
    private static String[] getAnnotationRequestMappingVale(Annotation annotation) {
        if (annotation != null) {
            return (String[]) AnnotationUtils.getValue(annotation);
        }
        return new String[0];
    }

    // generate showdoc
    private static void parseShowDoc(ControllerClassInfo controllerClassInfo) throws Exception {
        if (!HAD_PARSED_CLASS_MAP.contains(controllerClassInfo.getClass().getTypeName())) {
            // do parse
            JavaDocReader.parse(controllerClassInfo);
            HAD_PARSED_CLASS_MAP.add(controllerClassInfo.getControllerClazz().getTypeName());
        }
    }

    private static ShowDocModel getShowDocModel(MethodInfo methodInfo) throws Exception {
        // folder
        String folder = JavaDocReader.getFolder(methodInfo.getSourceClass().getTypeName());

        String methodName = methodInfo.getSourceClass().getTypeName() + "." + methodInfo.getMethod().getName();
        // title-
        String title = folder + "-" + JavaDocReader.getTitle(methodName);
        // method
        String requestMethod = methodInfo.getRequestMethod();
        //  request param
        String param = JavaDocReader.getRequestParam(methodName);
        // return model info
        String returnStr = JavaDocReader.getReturn(methodName);
        // example ,often a postman result,template:
        String reqJson = getBeanJSon(methodInfo.getParams().get(0).getParamterClass(),null);
        String respJson = getBeanJSon(methodInfo.getReturnInfo().getClazz(),methodInfo.getReturnInfo().getType());

//        buildUrl(requestUriPrefix, requestUriSuffix)
//        String content = ContentBuilder.newBuild().withRequestMethod(requestMethod).withRequestParam(param)
//                .withRequestReturn(returnStr).withTitle(title).withRequestUriPrefix(methodInfo.getUriPrefix())
//                .withRequestUriSuffix(methodInfo.getUrisSuffix())
//                .withExample(resultJson)
//                .build();
        ContentBuilder contentBuilder = ContentBuilder.builder()
                .requestMethod(requestMethod).reqParam(param)
                .respVO(returnStr).title(title).requestUrl(buildUrl(methodInfo.getUriPrefix(), methodInfo.getUrisSuffix()))
                .reqExample(reqJson)
                .respExample(respJson)
                .build();
        String content = GenerateFactory.generateApiContent(contentBuilder);
        ShowDocModel showDocModel = new ShowDocModel();
        showDocModel.setTitle(title);
        showDocModel.setFolder(folder);
        showDocModel.setContent(content);
        return showDocModel;
    }

    private static String getBeanJSon(Class<?> clazz,Type genericReturnType){
        PodamFactory factory = new PodamFactoryImpl();
        Object bean;
        if (genericReturnType instanceof ParameterizedTypeImpl) {
            bean = factory.manufacturePojo(clazz, ((ParameterizedTypeImpl) genericReturnType).getActualTypeArguments());
        } else {
            bean = factory.manufacturePojo(clazz, String.class);
        }
        return JSON.toJSONString(bean, true);
    }

    private static String buildUrl(String[] requestUriPrefix, String[] requestUriSuffix) {

        String apiHttpPrefix = ShowDocWorkUtil.API_HTTP_PREFIX;
        if (StrUtil.isBlank(apiHttpPrefix)) {
            apiHttpPrefix = "";
        }

        if (requestUriPrefix == null || requestUriPrefix.length == 0) {
            requestUriPrefix = new String[]{""};
        }
        StringBuilder build = new StringBuilder();
        for (String uriPrefix : requestUriPrefix) {
            for (String uriSuffix : requestUriSuffix) {
                build.append(apiHttpPrefix);
                build.append(uriPrefix);
                build.append(uriSuffix);
                build.append(",");
            }
        }
        return build.substring(0,build.length()-1);
    }

}
