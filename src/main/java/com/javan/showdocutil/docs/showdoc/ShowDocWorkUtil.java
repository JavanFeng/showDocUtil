package com.javan.showdocutil.docs.showdoc;

import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.javan.showdocutil.docs.showdoc.ShowDocConfiguration.*;

/**
 * @Desc 远程调用
 * @Author Javan Feng
 * @Date 2019 06 2019/6/29 19:59
 */
public class ShowDocWorkUtil {

    private final ShowDocConfiguration baseConfiguration;

    private static final RestTemplate TEMPLATE = new RestTemplate();

    private ShowDocWorkUtil(ShowDocConfiguration config) {
        Assert.notNull(config, "config must not be null");
        baseConfiguration = config;
    }

    public static ShowDocWorkUtil getInstance(ShowDocConfiguration config) {
        return new ShowDocWorkUtil(config);
    }

    public static ShowDocWorkUtil getInstance() {
        return new ShowDocWorkUtil(new ShowDocConfiguration());
    }

    public ShowDocWorkUtil withConsolePrint() {
        baseConfiguration.setConsolePrint(true);
        return this;
    }

    public ShowDocWorkUtil withApiHttpPrefix(String prefix){
        baseConfiguration.setApiCommonAddress(prefix);
        return this;
    }
    /**
     * 目录
     *
     * @param catalog 格式： 目录1/目录2
     * @return
     */
    public ShowDocWorkUtil withInCatalog(String catalog) {
        baseConfiguration.setCatalog(catalog);
        return this;
    }

    public ShowDocWorkUtil withUpdateShowDoc(String domain, String apiKey, String apiToken) {
        Assert.hasText(domain, "domian must not be null");
        Assert.hasText(apiKey, "apiKey must not be null");
        Assert.hasText(apiToken, "apiToken must not be null");
        baseConfiguration.setUpdateRemote(true);
        setDomain(domain);
        baseConfiguration.setApiKey(apiKey);
        baseConfiguration.setApiToken(apiToken);
        return this;
    }

    private void setDomain(String domain) {
        if (OFFICAL_DOMAIN.equals(domain) || OFFICAL_DOMAIN2.equals(domain)) {
            baseConfiguration.setAddressSuffix(REMOTE_ADDRESS_SUFFIX_2);
        } else {
            baseConfiguration.setAddressSuffix(REMOTE_ADDRESS_SUFFIX_1);
        }
        baseConfiguration.setRemoteDomain(domain);
    }

    /**
     * 基础包，扫描下面所有的controller层
     **/
    public void doWork(String basePackage) throws Exception {
        List<ShowDocModel> text = ControllerShowDocUtils.getText(basePackage,baseConfiguration);
        if (baseConfiguration.isConsolePrint()) {
            doConsolePrint(text);
        }

        if (baseConfiguration.isUpdateRemote()) {
            doUpdateRemote(text);
        }
    }


    /**
     * 扫描controller类
     **/
    public void doWork(Class<?> controllerClass) throws Exception {
        List<ShowDocModel> text = ControllerShowDocUtils.getText(controllerClass,baseConfiguration);
        if (baseConfiguration.isConsolePrint()) {
            doConsolePrint(text);
        }

        if (baseConfiguration.isUpdateRemote()) {
            doUpdateRemote(text);
        }
    }

    /**
     * 扫描controller类中的public 类
     **/
    public void doWork(Class<?> controllerClass, String simpleMethodName) throws Exception {
        ShowDocModel text = ControllerShowDocUtils.getText(controllerClass, simpleMethodName,baseConfiguration);
        if (baseConfiguration.isConsolePrint()) {
            doConsolePrint(Arrays.asList(text));
        }

        if (baseConfiguration.isUpdateRemote()) {
            doUpdateRemote(Arrays.asList(text));
        }
    }


    private void doUpdateRemote(List<ShowDocModel> text) {
        String urlStr;
        String remoteDomin = baseConfiguration.getRemoteDomain();
        final String addressSuffix = baseConfiguration.getAddressSuffix();
        if (remoteDomin != null && remoteDomin.contains("http")) {
            urlStr = remoteDomin + addressSuffix;
        } else {
            urlStr = REMOTE_ADDRESS_PREFIX + remoteDomin + addressSuffix;
        }
        String catalog = this.getCorrectCatalog();
        for (ShowDocModel showDocModel : text) {
            String folder = catalog + showDocModel.getFolder();
            String title = showDocModel.getTitle();
            String content = showDocModel.getContent();
            connect(urlStr, folder, title, content);
        }
    }

    private void connect(String url, String folder, String title, String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("api_key", baseConfiguration.getApiKey());
        params.add("api_token", baseConfiguration.getApiToken());
        params.add("cat_name", folder);
        params.add("page_content", content);
        params.add("page_title", title);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = TEMPLATE.exchange(url, HttpMethod.POST, requestEntity, String.class);
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            System.out.println("更新结果：" + response.getBody());
        } else {
            System.out.println("请求错误,HTTP_CODE:" + response.getStatusCodeValue() + ",信息：" + response.getBody());
        }
    }


    private void doConsolePrint(List<ShowDocModel> text) {
        String catalog = this.getCorrectCatalog();
        for (ShowDocModel showDocModel : text) {
            String folder = catalog + showDocModel.getFolder();
            String title = showDocModel.getTitle();
            String content = showDocModel.getContent();
            System.out.println("目录名：" + folder);
            System.out.println("页面名：" + title);
            System.out.println("markdown页：");
            System.out.println(content);
            System.out.println(System.lineSeparator());
        }
    }


    private String getCorrectCatalog() {
        String cata = baseConfiguration.getCatalog();
        if (cata != null) {
            if (!cata.endsWith("/")) {
                cata = cata + "/";
            }
        } else {
            cata = "";
        }
        return cata;
    }

    public ShowDocConfiguration getBaseConfiguration() {
        return baseConfiguration;
    }
}
