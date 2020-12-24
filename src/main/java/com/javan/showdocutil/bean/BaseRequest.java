package com.javan.showdocutil.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author lincc
 * @date 1.0 2020/11/13
 */
@Getter
@Setter
@NoArgsConstructor
public class BaseRequest implements Serializable {

    private static final long serialVersionUID = 6561221912087643368L;
    /**
     * 渠道来源
     */
    private String channelSource;

    /**
     * 登录平台
     */
    private String platform;

}
