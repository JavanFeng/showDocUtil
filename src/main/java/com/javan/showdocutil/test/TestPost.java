package com.javan.showdocutil.test;

import com.javan.showdocutil.controller.SystemUserController;
import com.javan.showdocutil.util.ShowDocWorkUtil;

/**
 * @author lincc
 * @version 1.0 2020/12/1
 */
public class TestPost {
    public static void main(String[] args) throws Exception {
        ShowDocWorkUtil.getInstance()
                .withApiHttpPrefix("https://rbttest.fingard.cn/api/installment")
//                .withInCatalog("车付保/接口功能设计/前后端接口/平台客户端")
                .withInCatalog("生成接口目录")
                .withUpdateShowDoc("www.showdoc.cc",
                        "86554fd751e6b15445d0b1e21d5caff219272901",
                        "6d40ed13bdde4af404d2b93283079604449302079")
                .withConsolePrint()
                .doWork(SystemUserController.class,"add");
    }

    // todo 模板加载需要优化
}
