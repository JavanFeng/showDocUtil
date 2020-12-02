package com.javan.showdocutil.controller;

import com.javan.showdocutil.bean.ResultContext;
import com.javan.showdocutil.bean.UserAddReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理
 *
 * @author lincc
 * @date 1.0 2020-11-13
 */
@Slf4j
@RestController
@RequestMapping("/system/user")
public class SystemUserController {

    /**
     * 新增
     */
    @PostMapping("/add.do")
    public ResultContext add(@RequestBody @Validated UserAddReq req) {
        return null;
    }
}
