package com.javan.showdocutil.model;

import java.lang.reflect.Type;
import java.util.List;

public class MethodReturnInfo extends AbstractParam {

    private Class<?> clazz;

    private Type type;

    public MethodReturnInfo(Class<?> clazz, Type type) {
        this.clazz = clazz;
        this.type = type;
    }

    public void addChild(ParamFieldInfo info) {
        children.add(info);
    }

    public Class<?> getClazz() {
        return clazz;
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public List<ParamFieldInfo> getChildren() {
        return children;
    }

    public void setChildren(List<ParamFieldInfo> children) {
        this.children = children;
    }

    @Override
    public Class<?> getParamterClass() {
        return clazz;
    }

    @Override
    public Boolean getConstraint() {
        return false;
    }

    @Override
    public Boolean getRequired() {
        return false;
    }
}
