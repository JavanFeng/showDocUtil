package com.javan.showdocutil.model;

public class ParamterInfoThreadLocal {

    private static ThreadLocal<ParamFieldInfo> PARSE_METHOD_SET = new ThreadLocal<>();

    public static void set(ParamFieldInfo methodInfo) {
        PARSE_METHOD_SET.set(methodInfo);
    }

    public static void remove() {
        PARSE_METHOD_SET.remove();
    }

    public static ParamFieldInfo get() {
        return PARSE_METHOD_SET.get();
    }
}