package com.javan.showdocutil.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 确定是否必传字段，暂不支持级联
 */
public class ParamFieldInfo extends AbstractParam implements ITypeVariable{
    /**
     * spring validation support，一般用于实体
     */
    private Boolean constraint;
    /**
     * 是否必须，直接在参数前加如@NotNull等注解
     */
    private Boolean required;
    /**
     * param
     */
    private Field parameter;


    public ParamFieldInfo(Boolean constraint, Boolean required, Field parameter) {
        this.constraint = constraint;
        this.required = required;
        this.parameter = parameter;
    }

    public ParamFieldInfo(Field parameter) {
        this.parameter = parameter;
    }


    public List<ParamFieldInfo> getChildren() {
        return super.children;
    }

    public void addChild(ParamFieldInfo paramInfo) {
        children.add(paramInfo);
    }

    public Field getParameter() {
        return parameter;
    }

    public void setParameter(Field parameter) {
        this.parameter = parameter;
    }

    public void setChildren(List<ParamFieldInfo> children) {
        this.children = children;
    }

    @Override
    public Boolean getConstraint() {
        return constraint;
    }

    public void setConstraint(Boolean constraint) {
        this.constraint = constraint;
    }

    @Override
    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public boolean isITypeVariable() {
        return false;
    }

    @Override
    public Class<?> getParamterClass() {
        return parameter.getClass();
    }
}