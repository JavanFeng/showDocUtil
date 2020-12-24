package com.javan.showdocutil.controller;

import com.javan.showdocutil.bean.ResultContext;
import com.javan.showdocutil.bean.UserAddReq;
import com.javan.showdocutil.test.TestInfo;
import com.javan.showdocutil.test.UserResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    /**
     * 编辑
     */
    @PostMapping("/edit.do")
    public ResultContext<List<UserResp>> edit(@RequestBody @Validated UserAddReq req) {
        return null;
    }

    /**
     * 查询详情
     */
    @PostMapping("/query.do")
    public ResultContext<TestInfo> query(@RequestBody @Validated UserAddReq req) {
        return null;
    }
}
