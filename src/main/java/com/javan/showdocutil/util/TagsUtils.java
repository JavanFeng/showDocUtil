package com.javan.showdocutil.util;

import com.sun.javadoc.Tag;

import java.util.Arrays;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-07-13
 * @desc tag util
 */
public class TagsUtils {

    private static final String SPRINT_TAG = "@sprint";
    private static final String DEPRECATED_TAG = "@deprecated";

    public static Tag getSprintTag(Tag[] tags){
        return Arrays.stream(tags).filter(e->e.name().equals(SPRINT_TAG)).findFirst().orElse(null);
    }

    public static boolean hashDeprecatedTag(Tag[] tags){
        return Arrays.stream(tags).anyMatch(e->e.name().equals(DEPRECATED_TAG));
    }

    private TagsUtils() {
    }
}
