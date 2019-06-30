package com.javan.showdocutil.util;

import com.sun.javadoc.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Desc 基本java类不需要解析的
 * @Author Javan Feng
 * @Date 2019 06 2019/6/25 22:47
 */
class BaseTypeUtil {
    /**
     * base type
     */
    private static final Set<String> BASETYPESET = new HashSet<>();

    /**
     * collection type
     */
    private static final Map<String, String> COLLECTIONS_TYPE = new HashMap<>();

    static {
        // base
        BASETYPESET.add(String.class.getName());
        BASETYPESET.add(Integer.class.getName());
        BASETYPESET.add(Double.class.getName());
        BASETYPESET.add(Date.class.getName());
        BASETYPESET.add(Float.class.getName());
        BASETYPESET.add(Byte.class.getName());
        BASETYPESET.add(Character.class.getName());
        BASETYPESET.add(Long.class.getName());
        BASETYPESET.add(Boolean.class.getName());
        BASETYPESET.add(boolean.class.getName());
        BASETYPESET.add(int.class.getName());
        BASETYPESET.add(long.class.getName());
        BASETYPESET.add(double.class.getName());
        BASETYPESET.add(float.class.getName());
        BASETYPESET.add(byte.class.getName());
        BASETYPESET.add(char.class.getName());
        BASETYPESET.add(LocalDate.class.getName());
        BASETYPESET.add(LocalDateTime.class.getName());
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
        return BASETYPESET.contains(typeName);
    }


    // 是否是基础类型
    public static String getActualTypeName(Type type) {
        return COLLECTIONS_TYPE.getOrDefault(type.qualifiedTypeName(), type.typeName());
    }

    private BaseTypeUtil() {
    }
}
