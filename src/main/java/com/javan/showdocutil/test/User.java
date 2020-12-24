package com.javan.showdocutil.test;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User extends BaseReq {
    private static final long serialVersionUID = -5289620605557430832L;

    private static final String CMD = "GGG";

    /**
     * 用户名
     */
    private String user_name;
    /**
     * 密码
     */
    private String password;
    /**
     * 姓名
     */
    @NotBlank
    private String nickname;
    /**
     * 手机号
     */
    @NotNull(message = "不能空")
    private String mobile;
    /**
     * 地址
     */
    private String address;
    /**
     * 单位
     */
    private String company;
    /**
     * 备注
     */
    private String remark;
    /**
     * 允许登录
     */
    private Boolean active;

    private String activeName;

    /**
     * 上次登录时间
     */
    private Date last_login_time;
    /**
     * 开启接收超标短信
     */
    private Boolean enable_sms = true;

    @Getter
    @Setter
    private List<String> list;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getActiveName() {
        return activeName;
    }

    public void setActiveName(String activeName) {
        this.activeName = activeName;
    }

    public Date getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(Date last_login_time) {
        this.last_login_time = last_login_time;
    }

    public Boolean getEnable_sms() {
        return enable_sms;
    }

    public void setEnable_sms(Boolean enable_sms) {
        this.enable_sms = enable_sms;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public static String getCMD() {
        return CMD;
    }
}
