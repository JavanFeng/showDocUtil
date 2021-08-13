package com.javan.showdocutil.docs.showdoc;

import com.javan.showdocutil.data.RequestDomain;

import java.util.List;

/**
 * @Desc TODO
 * @Author Javan Feng
 * @Date 2019 06 2019/6/27 21:38
 */
public class ContentBuilder {
    private ShowDocConfiguration baseConfiguration;

    private String title;

    private String description;

    private List<ShowReqRespModel> requestParam;

    private List<ShowReqRespModel> requestReturn;

    private List<ShowReqRespModel> commonParam;

    private String requestMethod;

    private String reqExample;

    private String businessDataName;

    private String respExample;

    private String[] urlList;

    private RequestDomain requestDomain;

    private ContentBuilder() {
    }

    public static ContentBuilder newBuild() {
        return new ContentBuilder();
    }

    public ContentBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ContentBuilder withRequestParam(List<ShowReqRespModel> requestParam) {
        this.requestParam = requestParam;
        return this;
    }

    public ContentBuilder withReqExample(String example) {
        this.reqExample = example;
        return this;
    }

    public ContentBuilder withRespExample(String example) {
        this.respExample = example;
        return this;
    }

    public ContentBuilder withRequestReturn(List<ShowReqRespModel> requestRetrun) {
        this.requestReturn = requestRetrun;
        return this;
    }

    public ContentBuilder withRequestReturnCommon(List<ShowReqRespModel> commonParam) {
        this.commonParam = commonParam;
        return this;
    }

    public ContentBuilder withRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public ContentBuilder withUrlList(String[] urlList) {
        this.urlList = urlList;
        return this;
    }

    public ContentBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public ContentBuilder withRequestDomain(RequestDomain requestDomain) {
        this.requestDomain = requestDomain;
        return this;
    }

    public ContentBuilder withConfiguration(ShowDocConfiguration baseConfiguration) {
        this.baseConfiguration = baseConfiguration;
        return this;
    }

    public ContentBuilder withBusinessName(String bussinessDataName) {
        this.businessDataName = bussinessDataName;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public List<ShowReqRespModel> getRequestParam() {
        return requestParam;
    }

    public List<ShowReqRespModel> getRequestReturn() {
        return requestReturn;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getReqExample() {
        return reqExample;
    }

    public String getRespExample() {
        return respExample;
    }

    public String[] getUrlList() {
        return urlList;
    }

    public String getDescription() {
        return description;
    }

    public RequestDomain getRequestDomain() {
        return requestDomain;
    }

    public ShowDocConfiguration getBaseConfiguration() {
        return baseConfiguration;
    }

    public List<ShowReqRespModel> getCommonParam() {
        return commonParam;
    }

    public String getBusinessDataName() {
        return businessDataName;
    }
}
