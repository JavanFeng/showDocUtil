package com.javan.showdocutil.model;

import com.javan.showdocutil.util.TypeVariableUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * 确定是否必传字段，暂不支持级联
 */
public class TypeVariableParamFieldInfo extends ParamFieldInfo {
    /**
     * 泛型类型
     */
    private Type clazz;

    private Class rawClazz;

    private Boolean stillTypeVariable;

    public TypeVariableParamFieldInfo(Type clazz, Boolean constraint, Boolean required, Field parameter) {
        super(constraint, required, parameter);
        this.clazz = clazz;
        this.stillTypeVariable = TypeVariableUtil.isParameterizedType(clazz);
        rawClazz = TypeVariableUtil.getParameterizedRawType(clazz);
    }

    public TypeVariableParamFieldInfo(Type clazz, Field parameter) {
        super(parameter);
        this.clazz = clazz;
        this.stillTypeVariable = TypeVariableUtil.isParameterizedType(clazz);
        rawClazz = TypeVariableUtil.getParameterizedRawType(clazz);
    }

    public Boolean getStillTypeVariable() {
        return stillTypeVariable;
    }

    public Class getRawClazz() {
        return rawClazz;
    }

    public void setRawClazz(Class rawClazz) {
        this.rawClazz = rawClazz;
    }

    public void setStillTypeVariable(Boolean stillTypeVariable) {
        this.stillTypeVariable = stillTypeVariable;
    }

    public Type getClazz() {
        return clazz;
    }

    public void setClazz(Type clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean isITypeVariable() {
        return true;
    }
    @Override
    public Class<?> getParamterClass() {
        return rawClazz;
    }
}