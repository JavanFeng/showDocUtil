package com.javan.showdocutil.model;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * 确定是否必传字段，暂不支持级联 TODO 合并fieldParam
 */
public class MethodParamInfo extends AbstractParam {
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
    private Parameter parameter;

    public MethodParamInfo(Boolean constraint, Boolean required, Parameter parameter) {
        this.constraint = constraint;
        this.required = required;
        this.parameter = parameter;
    }


    public List<ParamFieldInfo> getChildren() {
        return children;
    }

    public void addChild(ParamFieldInfo paramInfo) {
        children.add(paramInfo);

    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
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
    public Class<?> getParamterClass() {
        return parameter.getType();
    }
}