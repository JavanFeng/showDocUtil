package com.javan.showdocutil.util;

import com.javan.showdocutil.data.ControllerClazzAggre;
import com.javan.showdocutil.model.ControllerClassInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Desc showdoc工具类
 * @Author Javan Feng
 * @Date 2019 06 2019/6/23 0:32
 */
public class ControllerParseUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerParseUtils.class);
    /**
     * 扫描@Controller类
     */
    private static final ControllerClassPathScanner SCANNNER = new ControllerClassPathScanner();

    /**
     * loader
     */
    public static final ClassLoader CLASS_LOAD = ClassUtils.getDefaultClassLoader();

    // get content of text
    public static List<ControllerClazzAggre> getControllerClazzAggreByPath(String packagePath) throws Exception {
        Set<BeanDefinition> candidateComponents = SCANNNER.findCandidateComponents(packagePath);
        // className
        List<String> classNames = candidateComponents.stream().map(BeanDefinition::getBeanClassName).collect(Collectors.toList());
        //
        List<ControllerClazzAggre> showDocLists = new ArrayList<>();
        for (String className : classNames) {
            ControllerClazzAggre controllerClazzAggre = getControllerClazzAggreByClassName(className);
            showDocLists.add(controllerClazzAggre);
        }

        return showDocLists;
    }

    // get content of text
    public static ControllerClazzAggre getControllerClazzAggre(Class<?> aClass) throws Exception {
        LOGGER.info("正在读取Controller类信息....");
        if (isMatch(aClass)) {
            List<String> matchMethodNameList = filterMathMethodName(aClass);
            return parseJavaDoc(new ControllerClassInfo(matchMethodNameList, aClass));
        }
        return getControllerClazzAggreByClassName(aClass.getTypeName());
    }

    // get content of text
    public static ControllerClazzAggre getControllerClazzAggre(Class<?> aClass, String simpleMethodName) throws Exception {
        // clazz
        LOGGER.info("正在读取Controller类信息....");
        if (isMatch(aClass)) {
            List<String> matchMethodNameList = Collections.singletonList(simpleMethodName);
            return parseJavaDoc(new ControllerClassInfo(matchMethodNameList, aClass));
        }
        return null;
    }

    // parse the content
    private static ControllerClazzAggre getControllerClazzAggreByClassName(String className) throws Exception {
        // clazz
        LOGGER.info("正在读取Controller类信息....");
        Class<?> aClass = CLASS_LOAD.loadClass(className);
        return getControllerClazzAggre(aClass);
    }

    private static List<String> filterMathMethodName(Class<?> aClass) {
        return Arrays.stream(aClass.getDeclaredMethods())
                .filter(e -> AnnotationUtils.getAnnotation(e, RequestMapping.class) != null)
                .map(Method::getName).collect(Collectors.toList());
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

    private static ControllerClazzAggre parseJavaDoc(ControllerClassInfo controllerClassInfo) throws Exception {
            // do parse
            System.out.println("开始进行Doclet解析.....");
            return JavaDocReader.parse(controllerClassInfo);
    }
}
