package com.javan.showdocutil.model;

import java.util.ArrayList;
import java.util.List;

public class MethodReturnInfo_back implements IParam {

    private Class<?> clazz;

    private List<ParamFieldInfo> children = new ArrayList<>();

    public MethodReturnInfo_back(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void addChild(ParamFieldInfo info) {
        children.add(info);
    }

    public Class<?> getClazz() {
        return clazz;
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

    @Override
    public IParam getIparamByName(String simpleTypeName) {
        return null;
    }
}
