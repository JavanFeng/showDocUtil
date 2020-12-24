package com.javan.showdocutil.enums;

import lombok.Getter;

/**
 * @author lincc
 * @version 1.0 2020/12/1
 */
public enum ENParamType {
    /**
     * 1 请求参数
     * 2 返回参数
     */
    REQ("1","请求"),
    RESP("2","返回");

    @Getter
    private String value;
    @Getter
    private String label;

    ENParamType(String value,String label){
        this.value = value;
        this.label = label;
    }
}
