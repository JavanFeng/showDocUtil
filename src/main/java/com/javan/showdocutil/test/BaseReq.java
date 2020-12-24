package com.javan.showdocutil.test;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lincc
 * @version 1.0 2020/12/1
 */
@Data
public class BaseReq implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer version;
}
