package com.javan.showdocutil.model.build;

import com.javan.showdocutil.model.MethodReturnInfo;
import com.javan.showdocutil.model.ParamFieldInfo;
import com.javan.showdocutil.model.PlaceHolderParamFieldInfo;
import com.javan.showdocutil.model.TypeVariableParamFieldInfo;
import com.javan.showdocutil.test.UserController;
import com.javan.showdocutil.util.BaseTypeUtil;
import com.javan.showdocutil.util.TypeVariableUtil;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 获取方法中的所有字段信息 TODO: 各种情况下的返回值待优化
 */
public class MethodReturnInfoBuilder {

    private static final Logger LOG = Logger.getLogger("MethodParamInfoBuilder");


    // TODO 泛型
    public static MethodReturnInfo build(Method method) {
        Type genericReturnType = method.getGenericReturnType();
        // 一般为统一封装类带泛型
        Class<?> returnType = method.getReturnType();
        // 泛型 可空
        Type parameterizedType = TypeVariableUtil.getParameterizedType(method);
        MethodReturnInfo returnInfo = new MethodReturnInfo(returnType, genericReturnType);
        Field[] declaredFields = returnType.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Class<?> type1 = declaredField.getType();
            Type genericType = declaredField.getGenericType();
            ParamFieldInfo paramFieldInfo;
            if (genericType instanceof TypeVariableImpl) {
                paramFieldInfo = new TypeVariableParamFieldInfo(parameterizedType, declaredField);
                // 非基础  --->TODO: 循环依赖问题解决
                returnInfo.addChild(paramFieldInfo);
                setComposeParameterField(paramFieldInfo, null);
            } else {
                paramFieldInfo = new ParamFieldInfo(null, null, declaredField);
                returnInfo.addChild(paramFieldInfo);
                if (!BaseTypeUtil.isBaseType(type1.getTypeName())) {
                    // 非基础  --->TODO: 循环依赖问题解决
                    setComposeParameterField(paramFieldInfo, null);
                }
            }
        }

        return returnInfo;
    }


    private static void setComposeParameterField(ParamFieldInfo parent, Set<Class> refs) {

        if (parent.isITypeVariable()) {
            // 泛型？
            if (((TypeVariableParamFieldInfo) parent).getStillTypeVariable()) {
                // 泛型TODO
                handleTypeVariableField(parent, refs);
            } else {
                // clazz
                doClassFieldSet(parent, (Class<?>) ((TypeVariableParamFieldInfo) parent).getClazz(), refs);
            }
        } else {
            // clazz
            doClassFieldSet(parent, parent.getParameter().getType(), refs);
        }

    }

    private static void handleTypeVariableField(ParamFieldInfo parent, Set<Class> refs) {
        // 泛型TODO
        //Type parameterizedType = TypeVariableUtil.getParameterizedType(((TypeVariableParamFieldInfo) parent).getClazz());

        Type clazz = ((TypeVariableParamFieldInfo) parent).getClazz();
        if (clazz != null) {
            ParamFieldInfo parentChild = firstHandleTypeVariable(clazz);
            // raw
            thenHandleRaw(parent, parentChild);
        }
    }

    private static ParamFieldInfo firstHandleTypeVariable(Type parameterizedType) {
        // 泛型占位field
        ParamFieldInfo parentChild;
        if (parameterizedType instanceof ParameterizedType) {
            Type parameterizedRawType = TypeVariableUtil.getParameterizedType(parameterizedType);
            parentChild = new TypeVariableParamFieldInfo(parameterizedRawType, null);
            setComposeParameterField(parentChild, null);
        } else {
            parentChild = new PlaceHolderParamFieldInfo((Class) parameterizedType);
            Field[] declaredFields = ((Class) parameterizedType).getDeclaredFields();
            for (Field declaredField : declaredFields) {
                Class<?> type1 = declaredField.getType();
                Type genericType = declaredField.getGenericType();
                if (genericType instanceof TypeVariableImpl) {
                    ParamFieldInfo variableParamFieldInfo = new TypeVariableParamFieldInfo(parameterizedType, declaredField);
                    parentChild.addChild(variableParamFieldInfo);
                    setComposeParameterField(variableParamFieldInfo, null);
                } else {
                    ParamFieldInfo param = new ParamFieldInfo(null, null, declaredField);
                    parentChild.addChild(param);
                    if (!BaseTypeUtil.isBaseType(type1.getTypeName())) {
                        // 非基础  --->TODO: 循环依赖问题解决
                        setComposeParameterField(param, null);
                    }
                }
            }
        }
        return parentChild;
    }

    private static void thenHandleRaw(ParamFieldInfo parent, ParamFieldInfo parentChild) {
        Class<?> parameterizedRawType;
        if (parent instanceof TypeVariableParamFieldInfo) {
            parameterizedRawType = TypeVariableUtil.getParameterizedRawType(((TypeVariableParamFieldInfo) parent).getClazz());
        } else {
            parameterizedRawType = TypeVariableUtil.getParameterizedRawType(((PlaceHolderParamFieldInfo) parent).getClazz());
        }

        if (BaseTypeUtil.isCollectionType(parameterizedRawType.getTypeName())) {
            parent.addChild(parentChild);
        } else if (BaseTypeUtil.isBaseType(parameterizedRawType.getTypeName())) {
            throw new IllegalArgumentException("不支持Map等");
        }
        for (Field declaredField : parameterizedRawType.getDeclaredFields()) {
            Class<?> type1 = declaredField.getType();
            Type genericType = declaredField.getGenericType();
            if (genericType instanceof TypeVariableImpl) {
                parent.addChild(parentChild);
            } else if (genericType instanceof ParameterizedType) {
                // 仍旧是泛型; 这里就简单认为是集合，之后再考虑泛型的情况
                Class<?> raw = TypeVariableUtil.getParameterizedRawType(genericType);
                PlaceHolderParamFieldInfo placeHolderParamFieldInfo = new PlaceHolderParamFieldInfo(raw);
                parent.addChild(placeHolderParamFieldInfo);
                if (!BaseTypeUtil.isBaseType(raw.getTypeName())) {
                    thenHandleRaw(placeHolderParamFieldInfo, parentChild);
                } else {
                    // 基础 List set
                    placeHolderParamFieldInfo.addChild(parentChild);
                }
            } else {
                ParamFieldInfo param = new ParamFieldInfo(null, null, declaredField);
                parent.addChild(param);
                if (!BaseTypeUtil.isBaseType(type1.getTypeName())) {
                    // 非基础  --->TODO: 循环依赖问题解决
                    setComposeParameterField(param, null);
                }
            }
        }
    }

    private static void doClassFieldSet(ParamFieldInfo parent, Class<?> type, Set<Class> refs) {
        Field[] declaredFields = type.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Class<?> type1 = declaredField.getType();
            ParamFieldInfo paramFieldInfo = new ParamFieldInfo(declaredField);
            if (BaseTypeUtil.isBaseType(type1.getTypeName())) {
                parent.addChild(paramFieldInfo);
            } else {
                // 非基础  --->TODO: 循环依赖问题解决
                setComposeParameterField(paramFieldInfo, refs);
            }
        }
    }


    public static void main(String[] args) throws NoSuchFieldException {
        Method[] add = UserController.class.getMethods();
        Method method = add[1];
        MethodReturnInfo build = build(method);
        System.out.println("---");
       /* for (Field data : ApiResponse.class.getDeclaredFields()) {
            Class<?> type = data.getType();
            Type genericType = data.getGenericType();
            if (genericType instanceof TypeVariableImpl) {
                System.out.println(data.getName() + "是泛型");
            } else {
                System.out.println("不是泛型");
            }
        }*/
    }

}