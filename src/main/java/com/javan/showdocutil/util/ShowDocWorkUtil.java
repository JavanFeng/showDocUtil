package com.javan.showdocutil.util;

import org.springframework.util.Assert;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * @Desc 远程调用
 * @Author Javan Feng
 * @Date 2019 06 2019/6/29 19:59
 */
public class ShowDocWorkUtil {

    private static final String REMOTE_ADDRESS_PREFIX = "http://";

    private static final String REMOTE_ADDRESS_SUFFIX_1 = "/server/index.php?s=/api/item/updateByApi";

    private static final String REMOTE_ADDRESS_SUFFIX_2 = "/server/api/item/updateByApi";

    private static final String OFFICAL_DOMAIN = "www.showdoc.cc";
    /**
     * 控制台打印
     */
    private boolean consolePrint = false;
    /**
     * 更新远程showdoc
     */
    private boolean updateRemote = false;

    private String addressSuffix;

    /**
     * 更新远程
     */
    private String remoteDodmin, apiKey, apiToken;

    private ShowDocWorkUtil() {
    }


    public static ShowDocWorkUtil getInstance() {
        return new ShowDocWorkUtil();
    }

    public ShowDocWorkUtil withConsolePrint() {
        consolePrint = true;
        return this;
    }

    public ShowDocWorkUtil withUpdateShowDoc(String domain, String apiKey, String apiToken) {
        Assert.hasText(domain, "domian must not be null");
        Assert.hasText(apiKey, "apiKey must not be null");
        Assert.hasText(apiToken, "apiToken must not be null");
        updateRemote = true;
        setDomain(domain);
        this.apiKey = apiKey;
        this.apiToken = apiToken;
        return this;
    }

    private void setDomain(String domain) {
        if (OFFICAL_DOMAIN.equals(domain)) {
            addressSuffix = REMOTE_ADDRESS_SUFFIX_2;
        } else {
            addressSuffix = REMOTE_ADDRESS_SUFFIX_1;
        }
        this.remoteDodmin = domain;
    }

    /**
     * 基础包，扫描下面所有的controller层
     **/
    public void doWork(String basePackage) throws Exception {
        List<ShowDocModel> text = ControllerShowDocUtils.getText(basePackage);
        if (consolePrint) {
            doConsolePrint(text);
        }

        if (updateRemote) {
            doUpdateRemote(text);
        }
    }


    /**
     * 扫描controller类
     **/
    public void doWork(Class<?> controllerClass) throws Exception {
        List<ShowDocModel> text = ControllerShowDocUtils.getText(controllerClass);
        if (consolePrint) {
            doConsolePrint(text);
        }

        if (updateRemote) {
            doUpdateRemote(text);
        }
    }

    /**
     * 扫描controller类中的public 类
     **/
    public void doWork(Class<?> controllerClass, String simpleMethodName) throws Exception {
        ShowDocModel text = ControllerShowDocUtils.getText(controllerClass, simpleMethodName);
        if (consolePrint) {
            doConsolePrint(Arrays.asList(text));
        }

        if (updateRemote) {
            doUpdateRemote(Arrays.asList(text));
        }
    }


    private void doUpdateRemote(List<ShowDocModel> text) throws IOException {
        URL url = new URL(REMOTE_ADDRESS_PREFIX + remoteDodmin + addressSuffix);
        for (ShowDocModel showDocModel : text) {

            String folder = showDocModel.getFolder();
            String title = showDocModel.getTitle();
            String content = showDocModel.getContent();
            connect(url, folder, title, content);
        }
    }

    private void connect(URL url, String folder, String title, String content) throws IOException {
        //得到connection对象。
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置请求方式
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();
        try {
            // body
            String body = "api_key=" + apiKey + "&api_token=" + apiToken + "&cat_name=" + folder +
                    "&page_title=" + title + "&page_content=" + content;
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(body);
            writer.close();
            //得到响应码
            int responseCode = connection.getResponseCode();
            //将响应流转换成字符串
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String result = convert2String(connection);
                System.out.println("更新结果：" + result);
            } else {
                System.out.println("请求错误,HTTP_CODE:" + responseCode + ",信息：" + connection.getResponseMessage());
            }
        } finally {
            connection.disconnect();
        }
    }

    private String convert2String(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8")); // 实例化输入流，并获取网页代码
        String s; // 依次循环，至到读的值为空
        StringBuilder sb = new StringBuilder();
        while ((s = reader.readLine()) != null) {
            sb.append(s);
        }
        reader.close();
        return sb.toString();
    }

    private void doConsolePrint(List<ShowDocModel> text) {
        for (ShowDocModel showDocModel : text) {
            String folder = showDocModel.getFolder();
            String title = showDocModel.getTitle();
            String content = showDocModel.getContent();
            System.out.println("目录名：" + folder);
            System.out.println("页面名：" + title);
            System.out.println("markdown页：");
            System.out.println(content);
            System.out.println(System.lineSeparator());
        }
    }
}
