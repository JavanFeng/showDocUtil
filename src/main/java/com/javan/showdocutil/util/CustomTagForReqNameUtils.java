package com.javan.showdocutil.util;

import com.sun.javadoc.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-08-03
 */
public class CustomTagForReqNameUtils {
    private static final String SPRINT_TAG = "@sprint";
    private static final String IGNORE_TAG = "@ignore";
    private static final String DEPRECATED_TAG = "@deprecated";
    private static final String DESC_TAG = "@desc";
    private static final List<CustomTagHandler> HANLDERS = new ArrayList<>();

    public static void initHandle(CustomTagHandler customizable) {
        HANLDERS.add(customizable);
    }

    public static String getMethodDesc(Tag[] tags) {
        if (tags == null) {
            return null;
        }
        return Arrays.stream(tags).filter(e -> e.name().equals(DESC_TAG)).findFirst().map(Tag::text).orElse("");
    }

    public static Tag getSprintTag(Tag[] tags) {
        if (tags == null) {
            return null;
        }
        return Arrays.stream(tags).filter(e -> e.name().equals(SPRINT_TAG)).findFirst().orElse(null);
    }

    public static boolean isIgnore(Tag[] tags) {
        if (tags == null) {
            return false;
        }
        return Arrays.stream(tags).anyMatch(e -> e.name().equals(IGNORE_TAG));
    }

    public static boolean hashDeprecatedTag(Tag[] tags) {
        if (tags == null) {
            return false;
        }
        return Arrays.stream(tags).anyMatch(e -> e.name().equals(DEPRECATED_TAG));
    }

    static {
        CustomTagHandler sprintHandler = new CustomTagHandler() {
            @Override
            public boolean support(Tag[] tags) {
                return getSprintTag(tags) != null;
            }

            @Override
            public String handle(String name, Tag[] tags) {
                final Tag sprintTag = getSprintTag(tags);
                return name + "<sup><font color='red'>" + sprintTag.name() + sprintTag.text() + "</font></sup>";
            }
        };
        HANLDERS.add(sprintHandler);

        CustomTagHandler deprecatedHandler = new CustomTagHandler() {
            @Override
            public boolean support(Tag[] tags) {
                return hashDeprecatedTag(tags);
            }

            @Override
            public String handle(String name, Tag[] tags) {
                return "<s>" + name + "</s>";
            }
        };

        HANLDERS.add(deprecatedHandler);
    }

    public static String handleName(String name, Tag[] tags) {
        String afterName = name;
        for (CustomTagHandler handler : HANLDERS) {
            if (handler.support(tags)) {
                afterName = handler.handle(afterName, tags);
            }
        }
        return afterName;
    }

    private CustomTagForReqNameUtils() {
    }
}
