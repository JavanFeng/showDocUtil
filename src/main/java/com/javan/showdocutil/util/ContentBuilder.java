package com.javan.showdocutil.util;

import sun.reflect.generics.tree.ReturnType;

/**
 * @Desc TODO
 * @Author Javan Feng
 * @Date 2019 06 2019/6/27 21:38
 */
class ContentBuilder {

    private String title;

    private String requestParam;

    private String requestRetrun;

    private String requestMethod;

    private String[] requestUriPrefix;

    private String[] requestUriSuffix;

    private ContentBuilder() {
    }

    public static ContentBuilder newBuild() {
        return new ContentBuilder();
    }

    public ContentBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ContentBuilder withRequestParam(String requestParam) {
        this.requestParam = requestParam;
        return this;
    }

    public ContentBuilder withRequestReturn(String requestRetrun) {
        this.requestRetrun = requestRetrun;
        return this;
    }

    public ContentBuilder withRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public ContentBuilder withRequestUriPrefix(String[] requestUriPrefix) {
        this.requestUriPrefix = requestUriPrefix;
        return this;
    }

    public ContentBuilder withRequestUriSuffix(String[] requestUriSuffix) {
        this.requestUriSuffix = requestUriSuffix;
        return this;
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        // title
        buildValue(builder, "### 接口简要说明<br/>", title);
        // resquest url
        buildValue(builder, "### 请求URL<br/>", buildUrl(requestUriPrefix, requestUriSuffix));
        // method
        buildValue(builder, "### 请求方法<br/>", requestMethod);
        // request param
        builder.append("### 请求参数<br/>");
        builder.append(System.lineSeparator());
        builder.append("|参数名|必选|类型|说明");
        builder.append(System.lineSeparator());
        builder.append("|-|-|-|-");
        builder.append(System.lineSeparator());
        builder.append(requestParam);
        builder.append("<br/><br/>");
        builder.append(System.lineSeparator());
        // return
        builder.append("### 返回参数<br/>");
        builder.append(System.lineSeparator());
        builder.append("|参数名|必选|类型|说明");
        builder.append(System.lineSeparator());
        builder.append("|-|-|-|-");
        builder.append(System.lineSeparator());
        builder.append(requestRetrun);
        builder.append("<br/><br/>");
        builder.append(System.lineSeparator());
        // 返回示例 暂无只生成模板
        builder.append(" ### 返回示例<br/>");
        builder.append(System.lineSeparator());
        builder.append("```");
        builder.append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append("```");
        return builder.toString();
    }

    private void buildValue(StringBuilder builder, String explain, String value) {
        builder.append(explain);
        builder.append(System.lineSeparator());
        builder.append("-`");
        builder.append(value);
        builder.append("`<br/><br/>");
        builder.append(System.lineSeparator());
    }

    private String buildUrl(String[] requestUriPrefix, String[] requestUriSuffix) {
        if (requestUriPrefix == null || requestUriPrefix.length == 0) {
            requestUriPrefix = new String[]{""};
        }
        StringBuilder build = new StringBuilder();
        for (String uriPrefix : requestUriPrefix) {
            for (String uriSuffix : requestUriSuffix) {
                build.append(uriPrefix);
                build.append(uriSuffix);
                build.append(",");
            }
        }
        return build.toString();
    }
}
