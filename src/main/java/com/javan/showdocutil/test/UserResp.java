package com.javan.showdocutil.test;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserResp implements Serializable {
    private static final long serialVersionUID = -5289620605557430832L;

    /**
     * 主键id
     */
    private String urid;
    /**
     * 密码
     */
    private String password;
    /**
     * 姓名
     */
    private String nickname;
    /**
     * 手机号
     */
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

    /**
     * 版本号
     */
    private Integer version;

    /**
     * UserResp 用户信息
     */
    private List<UserResp> list;
}
