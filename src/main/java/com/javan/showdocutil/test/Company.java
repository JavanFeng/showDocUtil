package com.javan.showdocutil.test;

import java.util.List;

/**
 * 公司
 * @author fengjf
 * @version 1.0
 * @date 2021-07-28
 */
public class Company<T> {
    /** 公司名*/
    String name;
    /** 公司地址*/
    String address;
    /** 测试数据*/
    List<T> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
