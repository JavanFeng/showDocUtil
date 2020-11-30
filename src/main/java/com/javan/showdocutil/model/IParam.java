package com.javan.showdocutil.model;

/**
 * @author fengjf
 * @version 1.0
 * @date 2020-11-22
 * @desc TODO 合并
 */
public interface IParam {

    Class<?> getParamterClass();

    Boolean getConstraint();

    /**
     * 是否为必须字段
     */
    Boolean getRequired();

    IParam getIparamByName(String simpleTypeName);
}
