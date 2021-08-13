package com.javan.showdocutil.docs.showdoc;

import org.springframework.util.Assert;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-08-07
 * @desc 额外的配置
 */
public class ShowDocConfiguration {

    public static final String REMOTE_ADDRESS_PREFIX = "http://";

    public static final String REMOTE_ADDRESS_SUFFIX_1 = "/server/index.php?s=/api/item/updateByApi";

    public static final String REMOTE_ADDRESS_SUFFIX_2 = "/server/api/item/updateByApi";

    public static final String OFFICAL_DOMAIN = "www.showdoc.cc";
    public static final String OFFICAL_DOMAIN2 = "www.showdoc.com.cn";
    /**
     * 更新远程, catalog : 目录1/目录2/
     */
    private String addressSuffix,apiCommonAddress, remoteDomain, apiKey, apiToken, catalog;

    /**
     * 控制台打印
     */
    private boolean consolePrint = false;
    /**
     * 更新远程showdoc
     */
    private boolean updateRemote = false;
    /** 由于项目有可能会设置统一前缀*/
    private String urlPrefix;
    /** 文档中的示例数据，由于使用第三方生成随机，可能不利于前端理解。
     * 支持自定义一些数据。直接使用postman的json结果，用;隔开即可。
     * */
    private String mockJsonFileLocation="/mock/showdocMock.mk";
    /** 模板地址*/
    private String templateLocation = "/template/showdocTemplate.ftl";
    /**返回的公共参数类型，需要有一个泛型类型*/
    private Class<?> commonResultClazz;
    private String commonResultTypeName;

    public String getTemplateLocation() {
        return templateLocation;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public String getAddressSuffix() {
        return addressSuffix;
    }

    public void setAddressSuffix(String addressSuffix) {
        this.addressSuffix = addressSuffix;
    }

    public String getRemoteDomain() {
        return remoteDomain;
    }

    public void setRemoteDomain(String remoteDomain) {
        this.remoteDomain = remoteDomain;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public boolean isConsolePrint() {
        return consolePrint;
    }

    public void setConsolePrint(boolean consolePrint) {
        this.consolePrint = consolePrint;
    }

    public boolean isUpdateRemote() {
        return updateRemote;
    }

    public void setUpdateRemote(boolean updateRemote) {
        this.updateRemote = updateRemote;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public String getMockJsonFileLocation() {
        return mockJsonFileLocation;
    }

    public void setMockJsonFileLocation(String mockJsonFileLocation) {
        this.mockJsonFileLocation = mockJsonFileLocation;
    }

    public String getApiCommonAddress() {
        return apiCommonAddress;
    }

    public void setApiCommonAddress(String apiCommonAddress) {
        this.apiCommonAddress = apiCommonAddress;
    }

    public Class<?> getCommonResultClazz() {
        return commonResultClazz;
    }

    public <T>void setCommonResultClazz(Class<T> commonResultClazz) {
        // 简单实现
        Assert.isTrue(commonResultClazz.getTypeParameters().length == 1,"公共实体必须有一个泛型");
        this.commonResultClazz = commonResultClazz;
        this.commonResultTypeName = commonResultClazz.getTypeParameters()[0].getTypeName();
    }

    public String getCommonResultTypeName() {
        return commonResultTypeName;
    }
}
