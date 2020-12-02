package com.javan.showdocutil.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;


/**
 * @author lincc
 * @version 1.0 2020/12/1
 */
public class GenerateFactory {

    private static Template apiTemplate;
    private static Template extTemplate;

    static {
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setEncoding(Locale.getDefault(), "UTF-8");
        try {
            String realPath = GenerateFactory.class.getClassLoader().getResource("").getPath();
            System.out.println("当前加载目录"+realPath);
            configuration.setDirectoryForTemplateLoading(new File(realPath));
            apiTemplate = configuration.getTemplate("template/ApiContent.ftl");
            extTemplate = configuration.getTemplate("template/extContent.ftl");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateApiContent(ContentBuilder contentBuilder){
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(apiTemplate, contentBuilder);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateExtContent(Map<String,String> map){
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(extTemplate, map);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
