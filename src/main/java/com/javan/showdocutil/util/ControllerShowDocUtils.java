package com.javan.showdocutil.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Desc showdoc工具类
 * @Author Javan Feng
 * @Date 2019 06 2019/6/23 0:32
 */
class ControllerShowDocUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerShowDocUtils.class);
    /**
     * 扫描@Controller类
     */
    private static final ControllerClassPathScanner SCANNNER = new ControllerClassPathScanner();

    //
    private static final Set<String> HAD_PARSED_CLASS_MAP = new HashSet<>();

    /**
     * 扫描@Controller类
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
            showDocLists.addAll(showDocLists);
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
        if (isMatch(aClass)) {
            // 前缀
            String[] requestUriPrefix = getRequestUriPrefix(aClass);
            // method 信息
            List<MethodInfo> methods = getRequestMethodAndUriSuffix(aClass, requestUriPrefix);
            // 返回showdoc 所需信息
            Optional<MethodInfo> first = methods.stream().filter(methodInfo -> methodInfo.getMethod().getName().equals(simpleMethodName)).findFirst();
            if (!first.isPresent()) {
                LOGGER.info("not found the methd[{}] in class [{}]", simpleMethodName, aClass.getName());
                return null;
            } else {
                List<ShowDocModel> showDocModels = buildShowDocModelList(Arrays.asList(first.get()));
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
        Class<?> aClass = CLASS_LOAD.loadClass(className);
        if (isMatch(aClass)) {
            // 前缀
            String[] requestUriPrefix = getRequestUriPrefix(aClass);
            // method 信息
            List<MethodInfo> method = getRequestMethodAndUriSuffix(aClass, requestUriPrefix);
            // 返回showdoc 所需信息
            return buildShowDocModelList(method);
        }

        return Collections.emptyList();
    }

    // model
    private static List<ShowDocModel> buildShowDocModelList(List<MethodInfo> method) throws Exception {
        if (method == null || method.isEmpty()) {
            return Collections.emptyList();
        }

        List<ShowDocModel> list = new ArrayList<>();
        for (MethodInfo methodInfo : method) {
            list.add(parseShowDoc(methodInfo));
        }

        return list;
    }


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
                if (methods == null || methods.length == 0) {
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
                return getAnnotaionRequestMappingVaule(getMapping);
            case POST:
                PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
                return getAnnotaionRequestMappingVaule(postMapping);
            case PUT:
                PutMapping putMapping = AnnotationUtils.findAnnotation(method, PutMapping.class);
                return getAnnotaionRequestMappingVaule(putMapping);
            case PATCH:
                PatchMapping patchMapping = AnnotationUtils.findAnnotation(method, PatchMapping.class);
                return getAnnotaionRequestMappingVaule(patchMapping);
            case DELETE:
                GetMapping annotation = AnnotationUtils.findAnnotation(method, GetMapping.class);
                return getAnnotaionRequestMappingVaule(annotation);
            default:
                LOGGER.info("not support method,may met a problem with this program");
                return new String[0];

        }
    }

    // requestMapping value
    private static String[] getAnnotaionRequestMappingVaule(Annotation annotation) {
        if (annotation != null) {
            return (String[]) AnnotationUtils.getValue(annotation);
        }
        return new String[0];
    }


    static class MethodInfo {

        private String requestMethod;

        private String[] urisSuffix;

        private String[] uriPrefix;

        private Class<?> sourceClass;

        private Method method;

        public String getRequestMethod() {
            return requestMethod;
        }

        public void setRequestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public String[] getUrisSuffix() {
            return urisSuffix;
        }

        public void setUrisSuffix(String[] urisSuffix) {
            this.urisSuffix = urisSuffix;
        }

        public Class<?> getSourceClass() {
            return sourceClass;
        }

        public void setSourceClass(Class<?> sourceClass) {
            this.sourceClass = sourceClass;
        }

        public String[] getUriPrefix() {
            return uriPrefix;
        }

        public void setUriPrefix(String[] uriPrefix) {
            this.uriPrefix = uriPrefix;
        }
    }


    // generate showdoc
    private static ShowDocModel parseShowDoc(MethodInfo methodInfo) throws Exception {
        if (!HAD_PARSED_CLASS_MAP.contains(methodInfo.getSourceClass().getTypeName())) {
            // do parse
            JavaDocReader.parse(methodInfo.getSourceClass().getTypeName());
            HAD_PARSED_CLASS_MAP.add(methodInfo.getSourceClass().getTypeName());
        }
        // floder
        String floder = JavaDocReader.getFolder(methodInfo.getSourceClass().getTypeName());

        String methodName = methodInfo.getSourceClass().getTypeName() + "." + methodInfo.getMethod().getName();
        // title-
        String title = JavaDocReader.getTitle(methodName);
        // method
        String requestMethod = methodInfo.getRequestMethod();
        //  request param
        String param = JavaDocReader.getRequestParam(methodName);
        // return model info
        String returnStr = JavaDocReader.getReturn(methodName);
        // example ,often a postman result,template:
        String content = ContentBuilder.newBuild().withRequestMethod(requestMethod).withRequestParam(param)
                .withRequestReturn(returnStr).withTitle(title).withRequestUriPrefix(methodInfo.getUriPrefix())
                .withRequestUriSuffix(methodInfo.getUrisSuffix()).build();
        ShowDocModel showDocModel = new ShowDocModel();
        showDocModel.setTitle(title);
        showDocModel.setFolder(floder);
        showDocModel.setContent(content);
        return showDocModel;
    }
}
