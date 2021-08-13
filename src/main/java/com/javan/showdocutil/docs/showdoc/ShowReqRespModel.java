package com.javan.showdocutil.docs.showdoc;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-08-03
 * @desc TODO
 */
public class ShowReqRespModel {
//    | 变量名 | 变量描述 | 类型 | 必填 | 备注 |
    private String name;

    private String description;

    private String type;

    private String must;

    private String remark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMust() {
        return must;
    }

    public void setMust(String must) {
        this.must = must;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
