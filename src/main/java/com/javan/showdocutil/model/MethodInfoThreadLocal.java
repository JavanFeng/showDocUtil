package com.javan.showdocutil.model;

public class MethodInfoThreadLocal {

    private static ThreadLocal<MethodInfo> PARSE_METHOD_SET = new ThreadLocal<>();

    public static void set(MethodInfo methodInfo) {
        PARSE_METHOD_SET.set(methodInfo);
    }

    public static void remove() {
        PARSE_METHOD_SET.remove();
    }

    public static MethodInfo get() {
        return PARSE_METHOD_SET.get();
    }
}
