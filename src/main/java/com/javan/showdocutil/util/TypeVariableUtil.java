package com.javan.showdocutil.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public class TypeVariableUtil {

    /**
     * @return 仍旧可能是泛型
     */
    public static Type getParameterizedType(Method add) {
        Type genericReturnType = add.getGenericReturnType();
        if (genericReturnType instanceof ParameterizedType) {
            Type actualTypeArgument = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
            if (actualTypeArgument instanceof WildcardType) {
                // not support
                return null;
            }
            return actualTypeArgument;
        }
        return genericReturnType;
    }


    public static Type getParameterizedType(Type genericReturnType) {
        if (genericReturnType instanceof ParameterizedType) {
            Type actualTypeArgument = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
            if (actualTypeArgument instanceof WildcardType) {
                // not support
                return null;
            }
            return actualTypeArgument;
        }
        return genericReturnType;
    }

    public static Class<?> getParameterizedRawType(Type genericReturnType) {
        if (genericReturnType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) genericReturnType).getRawType();
            if (rawType instanceof WildcardType) {
                // not support
                return null;
            }
            return (Class<?>) rawType;
        }
        return (Class<?>) genericReturnType;
    }


    public static Boolean isParameterizedType(Type genericReturnType) {
        if(genericReturnType == null){
            return false;
        }

        return genericReturnType instanceof ParameterizedType;
    }
}
