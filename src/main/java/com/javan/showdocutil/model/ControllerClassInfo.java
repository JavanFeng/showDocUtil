package com.javan.showdocutil.model;

import java.util.List;

public class ControllerClassInfo {

    private List<MethodInfo> list;

    private Class<?> controllerClazz;

    public ControllerClassInfo(List<MethodInfo> list, Class<?> controllerClazz) {
        this.list = list;
        this.controllerClazz = controllerClazz;
    }

    public List<MethodInfo> getList() {
        return list;
    }

    public void setList(List<MethodInfo> list) {
        this.list = list;
    }

    public Class<?> getControllerClazz() {
        return controllerClazz;
    }

    public void setControllerClazz(Class<?> controllerClazz) {
        this.controllerClazz = controllerClazz;
    }

    // TODO: 简单判断 -——》之后应该加上参数 重载
    public MethodInfo getMethodByMethodName(String name) {
        if (list == null) {
            return null;
        }
       return list.stream().filter(m -> m.getMethod().getName().equals(name)).findFirst().orElse(null);
    }
}
