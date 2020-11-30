package com.javan.showdocutil.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 确定是否必传字段，暂不支持级联
 */
public class ParamFieldInfo_back extends AbstractParam implements ITypeVariable{
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


    private List<ParamFieldInfo_back> children = new ArrayList<>();


    public ParamFieldInfo_back(Boolean constraint, Boolean required, Field parameter) {
        this.constraint = constraint;
        this.required = required;
        this.parameter = parameter;
    }

    public ParamFieldInfo_back(Field parameter) {
        this.parameter = parameter;
    }


    public List<ParamFieldInfo_back> getChildren() {
        return children;
    }

    public void addChild(ParamFieldInfo_back paramInfo) {
        children.add(paramInfo);
    }

    public Field getParameter() {
        return parameter;
    }

    public void setParameter(Field parameter) {
        this.parameter = parameter;
    }

    public void setChildren(List<ParamFieldInfo_back> children) {
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

    @Override
    public IParam getIparamByName(String typeName) {
        if (children == null) {
            return null;
        }
        // find param
        for (ParamFieldInfo_back param : children) {
            if (param.getParameter().getType().getSimpleName().equals(typeName)) {
                return param;
            } else {
                ParamFieldInfo_back paramByName = findParamByName(param.getChildren(), typeName);
                if (paramByName != null) {
                    return paramByName;
                }
            }
        }
        return null;
    }

    private ParamFieldInfo_back findParamByName(List<ParamFieldInfo_back> children,
                                                String typeName) {
        if (children == null) {
            return null;
        }
        for (ParamFieldInfo_back child : children) {
            if (child.getParameter().getType().getSimpleName().equals(typeName)) {
                return child;
            } else {
                ParamFieldInfo_back paramByName = findParamByName(child.getChildren(), typeName);
                if (paramByName != null) {
                    return paramByName;
                }
            }
        }
        return null;
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