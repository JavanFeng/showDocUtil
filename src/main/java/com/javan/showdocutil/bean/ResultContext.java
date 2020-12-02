package com.javan.showdocutil.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Ruvik
 * @date 2020/11/12 11:27
 */
@Getter
@Setter
@NoArgsConstructor
public class ResultContext<T> implements Serializable {

    private static final long serialVersionUID = 5856432252782588227L;

    /**
     * 状态码，对应枚举ENMsgCode
     * 0：请求操作成功
     * 1：业务错误
     * 2：系统异常
     * 3：未登录
     * 4：session超时
     * 5：没有权限
     */
    private String code;

    /**
     * 提示信息
     */
    private String info;

    /**
     * 用于区分错误细节的值，比如登录失败的多种情况
     */
    private String value;

    /**
     * 成功标识
     */
    private Boolean success;

    /**
     * 需要返回的数据
     */
    private T data;

    public ResultContext(T data) {
        this();
        setData(data);
    }

    private ResultContext(String code, String info, String value, Boolean success) {
        this.code = code;
        this.info = info;
        this.value = value;
        this.success = success;
    }

    private ResultContext(String code, String info, Boolean success) {
        this.code = code;
        this.info = info;
        this.success = success;
    }

    public ResultContext(String code, String info, Boolean success, T data) {
        this.code = code;
        this.info = info;
        this.success = success;
        this.data = data;
    }
}
