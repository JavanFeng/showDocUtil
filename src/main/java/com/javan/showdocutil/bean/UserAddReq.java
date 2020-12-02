package com.javan.showdocutil.bean;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author lincc
 * @version 1.0 2020/11/13
 */
@Data
public class UserAddReq extends BaseRequest {
    private static final long serialVersionUID = -6339246987375540888L;

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "用户账号不能为空")
    private String mobile;

    @NotEmpty(message = "收款机构不能为空")
    private List<String> insurerConfigIdArray;

    @NotBlank(message = "组织不能为空")
    private String groupId;

    @NotBlank(message = "用户状态不能为空")
    private String userStatus;
}
