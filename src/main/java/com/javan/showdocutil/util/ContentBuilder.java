package com.javan.showdocutil.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Desc TODO
 * @Author Javan Feng
 * @Date 2019 06 2019/6/27 21:38
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentBuilder {

    private String title;

    private String reqParam;

    private String respVO;

    private String requestMethod;

    private String reqExample;

    private String respExample;

    private String requestUrl;

//    public String build() {
//        StringBuilder builder = new StringBuilder();
//        // title
//        buildValue(builder, "### 接口简要说明<br/>", title);
//        // resquest url
//        buildValue(builder, "### 请求URL<br/>", requestUrl);
//        // method
//        buildValue(builder, "### 请求方法<br/>", requestMethod);
//        // request param
//        builder.append("### 请求参数<br/>");
//        builder.append(System.lineSeparator());
//        builder.append("|参数名|必选|类型|说明");
//        builder.append(System.lineSeparator());
//        builder.append("|-|-|-|-");
//        builder.append(System.lineSeparator());
//        builder.append(requestParam);
//        builder.append("<br/><br/>");
//        builder.append(System.lineSeparator());
//        // return
//        builder.append("### 返回参数<br/>");
//        builder.append(System.lineSeparator());
//        builder.append("|参数名|必选|类型|说明");
//        builder.append(System.lineSeparator());
//        builder.append("|-|-|-|-");
//        builder.append(System.lineSeparator());
//        builder.append(requestReturn);
//        builder.append("<br/><br/>");
//        builder.append(System.lineSeparator());
//        // 返回示例 暂无只生成模板
//        builder.append(" ### 返回示例<br/>");
//        builder.append(System.lineSeparator());
//        builder.append("```");
//        builder.append(System.lineSeparator());
//        builder.append(example);
//        builder.append(System.lineSeparator());
//        builder.append("```");
//        return builder.toString();
//    }

//    private void buildValue(StringBuilder builder, String explain, String value) {
//        builder.append(explain);
//        builder.append(System.lineSeparator());
//        builder.append("-`");
//        builder.append(value);
//        builder.append("`<br/><br/>");
//        builder.append(System.lineSeparator());
//    }
}
