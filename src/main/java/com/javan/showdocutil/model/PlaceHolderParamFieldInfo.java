package com.javan.showdocutil.model;

/**
 * 泛型的占位类型
 */
public class PlaceHolderParamFieldInfo extends ParamFieldInfo {
    /**
     * param
     */
    private Class<?> clazz;


    public PlaceHolderParamFieldInfo(Class name) {
        super(null, null, null);
        this.clazz = name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean isITypeVariable() {
        return false;
    }

    @Override
    public Class<?> getParamterClass() {
        return clazz;
    }
}