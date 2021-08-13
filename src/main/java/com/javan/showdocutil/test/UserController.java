package com.javan.showdocutil.test;

import com.javan.showdocutil.docs.showdoc.ShowDocConfiguration;
import com.javan.showdocutil.docs.showdoc.ShowDocWorkUtil;
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
@RequestMapping({"/api/setting/auth/user","/api/mobile/user"})
@Validated
public class UserController{

    /**
     * 添加
     *
     * @param user 实体
     * @return 结果
     */
    @PostMapping({"/add","vip/add"})
    public ApiResponse<List<User>> add(@Valid @RequestBody User user) {
        return null;
    }


    /**
     * 删除
     *
     * @param id 用户id
     * @param testIdsList 测试id列表
     * @return 结果
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Company<User>> delete(@PathVariable("id") @NotNull(message = "id不能为空") Integer id, List<String> testIdsList) {
//        return null;
        return null;
    }


    public static void main(String[] args) throws Exception {
        final ShowDocWorkUtil instance = ShowDocWorkUtil.getInstance();
        final ShowDocConfiguration baseConfiguration = instance.getBaseConfiguration();
        baseConfiguration.setCommonResultClazz(ApiResponse.class);
        instance.withConsolePrint().doWork(UserController.class,"delete");
    }
}
