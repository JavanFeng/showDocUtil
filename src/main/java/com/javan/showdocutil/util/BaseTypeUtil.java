package com.javan.showdocutil.util;

import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import org.springframework.web.multipart.MultipartFile;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Desc 基本java类不需要解析的
 * @Author Javan Feng
 * @Date 2019 06 2019/6/25 22:47
 */
public class BaseTypeUtil {
    /**
     * base type
     */
    private static final Set<String> BASETYPESET = new HashSet<>();


    private static final Map<String,Class<?>> PRIMARY_MAP = new HashMap<>();

    /**
     * collection type
     */
    private static final Map<String, String> COLLECTIONS_TYPE = new HashMap<>();

    static {
        // base
        BASETYPESET.add(BigDecimal.class.getName());
        BASETYPESET.add(String.class.getName());
        BASETYPESET.add(Integer.class.getName());
        BASETYPESET.add(Double.class.getName());
        BASETYPESET.add(Date.class.getName());
        BASETYPESET.add(Float.class.getName());
        BASETYPESET.add(Byte.class.getName());
        BASETYPESET.add(Character.class.getName());
        BASETYPESET.add(Long.class.getName());
        BASETYPESET.add(Boolean.class.getName());
        PRIMARY_MAP.put(boolean.class.getName(),boolean.class);
        PRIMARY_MAP.put(int.class.getName(),int.class);
        PRIMARY_MAP.put(long.class.getName(),long.class);
        PRIMARY_MAP.put(double.class.getName(),double.class);
        PRIMARY_MAP.put(float.class.getName(),float.class);
        PRIMARY_MAP.put(byte.class.getName(),byte.class);
        PRIMARY_MAP.put(char.class.getName(),char.class);
        BASETYPESET.addAll(PRIMARY_MAP.keySet());
        BASETYPESET.add(LocalDate.class.getName());
        BASETYPESET.add(LocalDateTime.class.getName());
        BASETYPESET.add(MultipartFile.class.getName());
        //
        COLLECTIONS_TYPE.put(List.class.getName(), "array");
        COLLECTIONS_TYPE.put(Set.class.getName(), "array");
        COLLECTIONS_TYPE.put(ArrayList.class.getName(), "array");
        COLLECTIONS_TYPE.put(LinkedList.class.getName(), "array");
        COLLECTIONS_TYPE.put(HashSet.class.getName(), "array");
        COLLECTIONS_TYPE.put(Collection.class.getName(), "array");
        COLLECTIONS_TYPE.put(Map.class.getName(), "dict");
        COLLECTIONS_TYPE.put(HashMap.class.getName(), "dict");
        COLLECTIONS_TYPE.put(LinkedHashMap.class.getName(), "dict");
    }

    // 是否是基础类型
    public static boolean isBaseType(String typeName) {
        return typeName.contains("java.lang")
                || typeName.contains("java.math")
                || BASETYPESET.contains(typeName)
                || "void".equals(typeName);
    }

    // 是否是基础类型
    public static boolean isCollectionType(String typeName) {
        return typeName.contains("java.util");
    }

    public static boolean containMapType(Type[] typesInterfaces) {
        return Arrays.stream(typesInterfaces).anyMatch(e -> e.qualifiedTypeName().equals(Map.class.getName()));
    }

    // 是否是基础类型
    public static String getActualTypeName(Type type) {
        return COLLECTIONS_TYPE.getOrDefault(type.qualifiedTypeName(), type.typeName());
    }

    /**
     * 是基础泛型的集合
     */
    public static boolean isBaseListGeneric(Type type) {
        if (type == null) {
            return false;
        }
        if (isCollectionType(type.qualifiedTypeName())) {
            final ParameterizedType parameterizedType = type.asParameterizedType();
            if (parameterizedType != null) {
                final Type[] actualTypeArguments = parameterizedType.typeArguments();
                if (actualTypeArguments.length == 2) {
                    // 是map吗
                    if (containMapType(parameterizedType.interfaceTypes())) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (actualTypeArguments.length == 1) {
                    return isBaseType(actualTypeArguments[0].qualifiedTypeName());
                } else {
                    return false;
                }
            }
            // 其他类型不考虑
        }
        return false;
    }

    public static String getGenericTypeValues(Type type) {
        if (type == null) {
            return "";
        }
        final ParameterizedType parameterizedType = type.asParameterizedType();
        if (parameterizedType != null) {
            return Arrays.stream(parameterizedType.typeArguments())
                    .map(Type::typeName).collect(Collectors.joining(","));
        }
        return Object.class.getTypeName();
    }


    /**
     * 是基础泛型的集合
     */
    public static boolean isBaseListGeneric(java.lang.reflect.Type genericType) {
        if (genericType == null) {
            return false;
        }
        if (isCollectionType(genericType.getTypeName())) {
            if (genericType instanceof ParameterizedTypeImpl) {
                final java.lang.reflect.Type[] actualTypeArguments = ((ParameterizedTypeImpl) genericType).getActualTypeArguments();
                if (actualTypeArguments.length == 2) {
                    // 是map吗
                    if (genericType instanceof Map) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (actualTypeArguments.length == 1) {
                    return isBaseType(actualTypeArguments[0].getTypeName());
                } else {
                    return false;
                }
            }
            // 其他暂时不考虑
        }
        return false;
    }

    public static String getGenericTypeValues(java.lang.reflect.Type genericType) {
        if (genericType == null) {
            return "";
        }
        if (genericType instanceof ParameterizedTypeImpl) {
            return Arrays.stream(((ParameterizedTypeImpl) genericType).getActualTypeArguments())
                    .map(java.lang.reflect.Type::getTypeName).collect(Collectors.joining(","));
        }
        return Object.class.getTypeName();
    }


    public static Class<?> getPrimaryTypeClass(String typeName){
        return PRIMARY_MAP.get(typeName);
    }

    /** 添加基础类型*/
    public static boolean addBaseType(String typeName){
        return BASETYPESET.add(typeName);
    }

    private BaseTypeUtil() {
    }
}
