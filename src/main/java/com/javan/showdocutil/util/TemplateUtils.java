package com.javan.showdocutil.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import java.util.Locale;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-08-07
 */
public class TemplateUtils {

    private static final String DEFAULT_TEMPLATE_FTL = "/template/showdocTemplate.ftl";
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TemplateUtils.class);
    private static final SpringTemplateLoader springTemplateLoader = new SpringTemplateLoader(new DefaultResourceLoader(), "");


    public static String parse2Str(Object model, String templateLocation) {
        if (templateLocation == null || templateLocation.trim().length() == 0) {
            templateLocation = DEFAULT_TEMPLATE_FTL;
        }
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setTemplateLoader(springTemplateLoader);
        configuration.setEncoding(Locale.getDefault(), "UTF-8");
        try {
            final Template template = configuration.getTemplate(templateLocation);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            logger.error("未找到指定模板或者解析出错：" + templateLocation, e);
            throw new RuntimeException(e);
        }
    }

    private TemplateUtils() {
    }
}
