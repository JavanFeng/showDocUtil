package com.javan.showdocutil.util;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-08-05
 * @desc 实体对象的特殊处理，以区分传参
 */
public class ModelValueUtils {

    public static String getHtmlStrongValue(String value) {
        return "<strong>" + value + "</strong>";
    }

    public static String getMarkdownStrongValue(String value) {
        return "**" + value + "**";
    }

    public static String getMarkdownItalicParamValue(String value) {
        return "<font color='blue'>*" + value + "*</font>";
    }

    public static String getPrefixStyleValue(String prefix, String value) {
        if (prefix != null && prefix.trim().length() != 0) {
            return "<font color='gray'>" + value + "</font>";
        } else {
            return value;
        }
    }

    private ModelValueUtils() {
    }
}
