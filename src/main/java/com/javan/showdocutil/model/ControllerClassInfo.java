package com.javan.showdocutil.model;

import java.util.List;

public class ControllerClassInfo {

    private List<String> methodNameList;

    private Class<?> controllerClazz;

    public ControllerClassInfo(List<String> methodNameList, Class<?> controllerClazz) {
        this.methodNameList = methodNameList;
        this.controllerClazz = controllerClazz;
    }

    public Class<?> getControllerClazz() {
        return controllerClazz;
    }

    public void setControllerClazz(Class<?> controllerClazz) {
        this.controllerClazz = controllerClazz;
    }

    public List<String> getMethodNameList() {
        return methodNameList;
    }

    public void setMethodNameList(List<String> methodNameList) {
        this.methodNameList = methodNameList;
    }
}
