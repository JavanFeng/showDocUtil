package com.javan.showdocutil.model;

public class ControllerClazzThreadLocal {

    private static ThreadLocal<ControllerClassInfo> PARSE_METHOD_SET = new ThreadLocal<>();

    public static void set(ControllerClassInfo methodInfo) {
        PARSE_METHOD_SET.set(methodInfo);
    }

    public static void remove() {
        PARSE_METHOD_SET.remove();
    }

    public static ControllerClassInfo get() {
        return PARSE_METHOD_SET.get();
    }
}
