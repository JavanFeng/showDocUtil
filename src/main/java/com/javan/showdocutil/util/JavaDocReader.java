package com.javan.showdocutil.util;

import com.javan.showdocutil.constant.BuildParentEnums;
import com.javan.showdocutil.data.*;
import com.javan.showdocutil.data.base.AbstractDocument;
import com.javan.showdocutil.docs.showdoc.ControllerShowDocUtils;
import com.javan.showdocutil.model.ControllerClassInfo;
import com.sun.javadoc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class JavaDocReader {

    private static final Logger logger = LoggerFactory.getLogger(JavaDocReader.class);
    /**
     * 返回参数内容 TODO 循环引用解决
     **/
    private static final Stack<String> CLASS_CYCLE_SET = new Stack<>();

    /**
     * 显示DocRoot中的基本信息
     */
    private static ControllerClazzAggre doParse(ControllerClassInfo methodInfo,
                                                RootDoc rootDoc) throws Exception {
        // 只有一个
        ClassDoc[] classes = rootDoc.classes();
        // 后续不需要root 可以覆盖
        ClassDoc aClass = classes[0];
        // clazz
        return ControllerClazzAggre
                .buildControllerClazzAggre(methodInfo, aClass);
    }

    /**
     * build method
     */
    public static List<MethodDomain> buildMethodDomainList(List<String> methodNameList, Class<?> controllerClazz, ClassDoc aClass) throws Exception {
        RequestMapping clazzMapping = controllerClazz.getAnnotation(RequestMapping.class);
        // method todo 这边可能需要支持重载
        final Map<String, Method> nameAndMethodMap = Arrays.stream(controllerClazz.getDeclaredMethods()).collect(Collectors.toMap(Method::getName, e -> e));
        MethodDoc[] methods = aClass.methods();
        List<MethodDomain> methodDomainList = new ArrayList<>(methodNameList.size());
        for (MethodDoc methodDoc : methods) {
            if (!methodNameList.contains(methodDoc.name())) {
                continue;
            }
            final Method method = nameAndMethodMap.get(methodDoc.name());
            if (method != null) {
                // 添加页面标题
                MethodDomain methodDomain = MethodDomain.buildMethodDomain(clazzMapping, method, methodDoc);
                methodDomainList.add(methodDomain);
            }
        }
        return methodDomainList;
    }

    public static PojoDomain addRequestReturn(MethodDoc methodDoc) throws Exception {
        // 方法名
        Type type = methodDoc.returnType();
        final PojoDomain returnPojo = PojoDomain.buildPojoDomainReturn(methodDoc);
        if (!BaseTypeUtil.isBaseType(type.qualifiedTypeName())) {
            List<AbstractDocument> paramContentList = new ArrayList<>();
            JavaDocReader.doParseType(new Type[]{type}, paramContentList);
            returnPojo.setFields(paramContentList);
        }
        return returnPojo;
    }

    public static List<ParameterDomain> addRequestParam(Method method, MethodDoc methodDoc) throws Exception {
        // 参数
        final Map<String, java.lang.reflect.Parameter> nameAndParameterMap = Arrays.stream(method.getParameters())
                .collect(Collectors.toMap(java.lang.reflect.Parameter::getName, e -> e));
        // 标签名
        ParamTag[] paramTags = methodDoc.paramTags();
        final Map<String, Parameter> nameAndParameterDocMap = Arrays.stream(methodDoc.parameters())
                .collect(Collectors.toMap(Parameter::name, e -> e));
        List<ParameterDomain> parameterDomainList = new ArrayList<>(nameAndParameterMap.size());
        for (ParamTag paramTag : paramTags) {
            final java.lang.reflect.Parameter parameter = nameAndParameterMap.get(paramTag.parameterName());
            final Parameter parameterDoc = nameAndParameterDocMap.get(paramTag.parameterName());
            if (parameter != null) {
                final ParameterDomain domain = ParameterDomain.buildParameterDomain(parameter, parameterDoc, paramTag);
                parameterDomainList.add(domain);
            }
        }
        return parameterDomainList;
    }


    /**
     * 获取请求参数内容
     **/
    public static List<AbstractDocument> getParamText(Parameter parameter) throws Exception {
        // 解析实体
        List<AbstractDocument> paramContentList = new ArrayList<>();
        doParseType(new Type[]{parameter.type()}, paramContentList);
        return paramContentList;
    }


    static void doParseType(Type[] types, List<AbstractDocument> paramContentList) throws Exception {
        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            doBuildPojos(paramContentList, type, BuildParentEnums.NO_BUILD, Collections.emptyMap());
        }
    }

    static void doFieldParseType(Type[] types, List<AbstractDocument> paramContentList,
                                 Map<String, Type> genericTypeAndTypeName) throws Exception {
        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            doBuildPojos(paramContentList, type, BuildParentEnums.NO_BUILD,genericTypeAndTypeName);
        }
    }

    private static Map<String, Type> doBuildGenericMap(TypeVariable[] typeVariables, Type[] typesArguments) {
        Map<String, Type> typeAndClazzNameMap = new HashMap<>(typeVariables.length);
        for (int i = 0; i < typeVariables.length; i++) {
            if (i >= typesArguments.length) {
                // ignore
                typeAndClazzNameMap.put(typeVariables[i].typeName(), JDocletParseUtil.OBJECT);
            } else {
                typeAndClazzNameMap.put(typeVariables[i].typeName(), typesArguments[i]);
            }
        }
        return typeAndClazzNameMap;
    }

    private static void doBuildPojos(List<AbstractDocument> paramContentList,
                                     Type type, BuildParentEnums enums,
                                     Map<String, Type> genericTypeAndTypeName) throws Exception {
        // 结束
        if (!BaseTypeUtil.isBaseType(type.qualifiedTypeName())) {
            // collection
            ParameterizedType parameterizedType = type.asParameterizedType();
            if (BaseTypeUtil.isCollectionType(type.qualifiedTypeName())) {
                Type[] typesArguments = parameterizedType.typeArguments();
                if (typesArguments != null && typesArguments.length > 0) {
                    for (Type typesArgument : typesArguments) {
                        final Type argType = genericTypeAndTypeName.getOrDefault(typesArgument.qualifiedTypeName(), typesArgument);
                        doCycleBuildPojos(paramContentList, type, argType, enums);
                    }
                } else {
                    doCycleBuildPojos(paramContentList, type, JDocletParseUtil.OBJECT, enums);
                }
            } else {
                checkDuplicationAndBuild(paramContentList, type, parameterizedType, enums);
            }
        } else {
            // base type
            paramContentList.add(PojoDomain.buildBasePojoDomain(type));
        }
    }

    private static void doCycleBuildPojos(List<AbstractDocument> paramContentList, Type type,
                                          Type typesArgument, BuildParentEnums enums) throws Exception {
        if (enums == BuildParentEnums.NO_BUILD) {
            doBuildPojos(paramContentList, typesArgument, BuildParentEnums.BUILD, Collections.emptyMap());
        } else {
            PojoDomain domain = new PojoDomain();
            paramContentList.add(domain);
            domain.setClazz(ControllerShowDocUtils.CLASS_LOAD.loadClass(type.qualifiedTypeName()));
            domain.setComment(type.typeName());
            domain.setDefineName(type.typeName());
            List<AbstractDocument> paramList = new ArrayList<>();
            domain.setFields(paramList);
            doBuildPojos(paramList, typesArgument, BuildParentEnums.BUILD, Collections.emptyMap());
        }
    }

    private static void checkDuplicationAndBuild(List<AbstractDocument> paramContentList,
                                                 Type type,
                                                 ParameterizedType parameterizedType,
                                                 BuildParentEnums enums) throws Exception {
        // class model
        final boolean cycle = checkCycleParse(type.qualifiedTypeName());
        // may cycle
        if (!cycle) {
            // type is 泛型
            try {
                Map<String, Type> typeAndTypeName = new HashMap<>();
                if (parameterizedType != null) {
                    Type[] typesArguments = parameterizedType.typeArguments();
                    // company<T,H,P>
                    final ClassDoc classDoc = type.asClassDoc();
                    final TypeVariable[] typeVariables = classDoc.typeParameters();
                    typeAndTypeName = doBuildGenericMap(typeVariables, typesArguments);
                }
                // null getModelText
                final List<PojoDomain> modelText = getModelText(type.qualifiedTypeName(), typeAndTypeName);
                if (enums == BuildParentEnums.NO_BUILD) {
                    paramContentList.addAll(modelText.get(0).getChildren());
                } else {
                    paramContentList.addAll(modelText);
                }
            } finally {
                CLASS_CYCLE_SET.pop();
            }
        } else {
            PojoDomain pojoDomain = new PojoDomain();
            pojoDomain.setFields(Collections.emptyList());
            pojoDomain.setDefineName(type.qualifiedTypeName());
            pojoDomain.setComment("循环实体");
            pojoDomain.setCycle(true);
            pojoDomain.setClazz(ControllerShowDocUtils.CLASS_LOAD.loadClass(type.qualifiedTypeName()));
            paramContentList.add(pojoDomain);
        }
    }

    private static void clearCycleSet() {
        CLASS_CYCLE_SET.clear();
    }

    private static boolean checkCycleParse(String qualifiedTypeName) {
        if (CLASS_CYCLE_SET.contains(qualifiedTypeName)) {
//            throw new IllegalArgumentException("不支持解析循环引用的实体[" + qualifiedTypeName + "]");
            logger.warn("发现循环引用的实体[{}]", qualifiedTypeName);
            return true;
        } else {
            CLASS_CYCLE_SET.push(qualifiedTypeName);
        }
        return false;
    }

    public JavaDocReader() {

    }

    /**
     * 解析controller
     */
    public static ControllerClazzAggre parse(ControllerClassInfo methodInfo) throws Exception {
        Class<?> sourceClass = methodInfo.getControllerClazz();
        RootDoc rootDoc = JDocletParseUtil.parseDoc(sourceClass);
        return doParse(methodInfo, rootDoc);
    }


    // 获取实体类
    // https://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#sourcepath
    public static List<PojoDomain> getModelText(String typeName, Map<String, Type> typeAndTypeName) throws Exception {
        return executeGetModelText(typeName, typeAndTypeName);
    }

    // 获取实体类
    // https://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#sourcepath
    private static List<PojoDomain> executeGetModelText(String typeName, Map<String, Type> genericTypeAndTypeName) throws Exception {
        Class<?> paramterClass = ControllerShowDocUtils.CLASS_LOAD.loadClass(typeName);
        final RootDoc rootDoc = JDocletParseUtil.parseDoc(paramterClass);
        return doGetModelText(genericTypeAndTypeName, rootDoc);
    }

    private static boolean isGenericType(String typeName) {
        // 先考虑一个的为泛型
        return typeName.split("\\.").length == 1;
    }

    private static List<PojoDomain> doGetModelText(Map<String, Type> genericTypeAndTypeName, RootDoc rootDoc) throws Exception {
        ClassDoc[] classes = rootDoc.classes();
        // 只会存在一个
        ClassDoc aClass = classes[0];
        final Class<?> clazz = ControllerShowDocUtils.CLASS_LOAD.loadClass(aClass.qualifiedTypeName());
        final PojoDomain domain = PojoDomain.buildPojoDomain(clazz, aClass);
        final Map<String, Field> filedNameAndFieldMap = Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, e -> e));
        FieldDoc[] fields = aClass.fields();
        List<FieldDomain> fieldList = new ArrayList<>();
        domain.setFields(fieldList);
        for (FieldDoc fieldDoc : fields) {
            String name = fieldDoc.name();
            Type type = fieldDoc.type();
            String typeName = type.qualifiedTypeName();
            String genericTypeName=null;
            if (!type.isPrimitive() && isGenericType(typeName)) {
                type = genericTypeAndTypeName.get(typeName);
                genericTypeName =typeName;
            }
            final Field field = filedNameAndFieldMap.get(name);
            if (field != null) {
                if (type == null) {
                    // object 泛型未直指定
                    FieldDomain fieldDomain = new FieldDomain();
                    fieldDomain.setChildren(Collections.emptyList());
                    fieldDomain.setComment(fieldDoc.commentText());
                    fieldDomain.setDefineName(fieldDoc.name());
                    fieldDomain.setField(field);
                    fieldList.add(fieldDomain);
                } else {
                        final FieldDomain fieldDomain = FieldDomain
                                .buildFieldDomain(field, fieldDoc, type, genericTypeName,genericTypeAndTypeName);
                        fieldList.add(fieldDomain);
//                    }
                }
            }
        }
        return Collections.singletonList(domain);
    }

    public static List<AbstractDocument> getPojoDomainByDocType(Type type, Map<String, Type> genericTypeAndTypeName) throws Exception {
        List<AbstractDocument> paramContentList = new ArrayList<>();
        String ty = type.qualifiedTypeName();
        if (!BaseTypeUtil.isBaseType(ty)) {
            JavaDocReader.doFieldParseType(new Type[]{type}, paramContentList,genericTypeAndTypeName);

        }
        return paramContentList;
    }
}