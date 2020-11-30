package com.javan.showdocutil.model.build;

import com.javan.showdocutil.model.MethodParamInfo;
import com.javan.showdocutil.model.ParamFieldInfo;
import com.javan.showdocutil.util.BaseTypeUtil;
import org.springframework.validation.annotation.Validated;

import javax.validation.Constraint;
import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 获取方法中的所有字段信息
 */
public class MethodParamInfoBuilder {

    private static final Logger LOG = Logger.getLogger("MethodParamInfoBuilder");


    public static List<MethodParamInfo> build(Method method) {
        List<MethodParamInfo> params = new ArrayList<>();
        int parameterCount = method.getParameterCount();
        if (0 == parameterCount) {
            return params;
        }
        // 总的
        Boolean isConstraint = method.getAnnotation(Validated.class) != null || method.getAnnotation(Valid.class) != null;

        for (Parameter parameter : method.getParameters()) {
            Boolean paramConstraint = parameter.getAnnotation(Valid.class) != null || method.getAnnotation(Validated.class) != null;
            Boolean required = hasConstraint(parameter.getDeclaredAnnotations(), new HashSet<>());
            Class<?> type = parameter.getType();
            // TODO 泛型处理
            if (BaseTypeUtil.isBaseType(type.getTypeName())) {
                MethodParamInfo methodParamInfo = new MethodParamInfo(isConstraint || paramConstraint, required, parameter);
                params.add(methodParamInfo);
            } else {
                // 实体类
                MethodParamInfo methodParamInfo = new MethodParamInfo(isConstraint || paramConstraint, required, parameter);
                params.add(methodParamInfo);
                setComposeParameter(methodParamInfo, paramConstraint, new HashSet<>());
            }
        }

        return params;
    }

    private static void setComposeParameter(MethodParamInfo parent, Boolean parentConstraint, Set<Class> refs) {
        Parameter parameter = parent.getParameter();
        Class<?> type = parameter.getType();
        Field[] declaredFields = type.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Class<?> type1 = declaredField.getType();
            Boolean paramConstraint =parameter.getAnnotation(Valid.class) != null;
            Boolean required = hasConstraint(declaredField.getDeclaredAnnotations(), new HashSet<>());
            ParamFieldInfo paramFieldInfo = new ParamFieldInfo(paramConstraint, parentConstraint && required, declaredField);
            if (BaseTypeUtil.isBaseType(type1.getTypeName())) {
                parent.addChild(paramFieldInfo);
            } else {
                // 非基础  --->TODO: 循环依赖问题解决
                setComposeParameterField(paramFieldInfo,paramConstraint,refs);
            }
        }
    }

    // TODO:merge above
    private static void setComposeParameterField(ParamFieldInfo parent, Boolean parentConstraint, Set<Class> refs) {
        Field parameter = parent.getParameter();
        Class<?> type = parameter.getType();
        Field[] declaredFields = type.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Class<?> type1 = declaredField.getType();
            Boolean paramConstraint = parameter.getAnnotation(Valid.class) != null;
            Boolean required = hasConstraint(declaredField.getDeclaredAnnotations(), new HashSet<>());
            ParamFieldInfo paramFieldInfo = new ParamFieldInfo(paramConstraint, parentConstraint && required, declaredField);
            if (BaseTypeUtil.isBaseType(type1.getTypeName())) {
                parent.addChild(paramFieldInfo);
            } else {
                // 非基础  --->TODO: 循环依赖问题解决
                setComposeParameterField(paramFieldInfo,paramConstraint,refs);
            }
        }
    }


    private static Boolean hasConstraint(Annotation[] declaredAnnotations, Set<Annotation> visit) {
        if (declaredAnnotations == null || declaredAnnotations.length == 0) {
            return false;
        }
        for (Annotation declaredAnnotation : declaredAnnotations) {
            if (visit.contains(declaredAnnotation)) {
                continue;
            }
            if (isInJavaLangAnnotationPackage(declaredAnnotation.annotationType().getTypeName())) {
                continue;
            }
            visit.add(declaredAnnotation);
            if (Constraint.class.equals(declaredAnnotation.annotationType())) {
                return true;
            } else {
                Annotation[] de = declaredAnnotation.annotationType().getDeclaredAnnotations();
                Boolean result = hasConstraint(de, visit);
                if (result) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isInJavaLangAnnotationPackage(String annotationType) {
        return (annotationType != null && annotationType.startsWith("java.lang.annotation"));
    }

}