package com.javan.showdocutil.test;

import com.javan.showdocutil.util.ShowDocWorkUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户信息
 *
 * @version 1.0
 * @author: fengjf
 * @date: 2019/8/14 10:04
 */
@RestController
@RequestMapping("/api/setting/auth/user")
@Validated
public class UserController {

    /**
     * 添加
     *
     * @param user 实体
     * @return 结果
     */
    @PutMapping("/add")
    public ApiResponse add(@Valid @RequestBody User user) {
        return null;
    }



    /**
     * 删除
     *
     * @param id id
     * @return 结果
     */
    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") @NotNull(message = "id不能为空") Integer id) {
        return null;
    }


    public static void main(String[] args) throws Exception {
        ShowDocWorkUtil.getInstance().withConsolePrint().doWork(UserController.class,"add");
    }
}
