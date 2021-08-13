package com.javan.showdocutil.util;

import com.sun.javadoc.Tag;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-08-03
 */
public interface CustomTagHandler {

    boolean support(Tag[] tags);

    String handle(String name, Tag[] tags);
}
