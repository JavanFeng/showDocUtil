package com.javan.showdocutil.test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xx
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {
    /**
     * @ignore
     */
    private static final long serialVersionUID = -8990271803450171144L;
    private Boolean success = true;
    /** 数据*/
    private T data;
    private Object additional_data;
    private Object related_objects;
    /** 信息*/
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date request_time = new Date();
    @JsonIgnore
    private Object attach;
    /**
     * Gets the value of attach.
     *
     * @return the value of attach
     */
    public Object getAttach() {
        return attach;
    }

    /**
     * Sets the attach.
     *
     * <p>You can use getAttach() to get the value of attach</p>
     *
     * @param attach attach
     */
    public void setAttach(Object attach) {
        this.attach = attach;
    }

    public ApiResponse() {
    }

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(T data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getAdditional_data() {
        return additional_data;
    }

    public void setAdditional_data(Object additional_data) {
        this.additional_data = additional_data;
    }

    public Object getRelated_objects() {
        return related_objects;
    }

    public void setRelated_objects(Object related_objects) {
        this.related_objects = related_objects;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Gets the value of request_time.
     *
     * @return the value of request_time
     */
    public Date getRequest_time() {
        return request_time;
    }

    /**
     * Sets the request_time.
     *
     * <p>You can use getRequest_time() to get the value of request_time</p>
     *
     * @param request_time request_time
     */
    public void setRequest_time(Date request_time) {
        this.request_time = request_time;
    }
}
