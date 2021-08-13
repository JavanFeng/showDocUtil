package com.javan.showdocutil.data.base;

import com.sun.javadoc.Tag;

import java.util.List;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-07-27
 * @desc 基础
 */
public abstract class AbstractDocument {
    /** 定义的名字：参数*/
    private String defineName;
    /** 解释*/
    private String comment;
    /** 用于自定义的tag，比如@sprint:迭代，@deprecated:废弃等*/
    private Tag[] tags;
    /** 是否为循环实体，不解析*/
    private boolean isCycle = false;

    public abstract List<? extends AbstractDocument> getChildren();

    public abstract String getActualTypeName();

    public String getDefineName() {
        return defineName;
    }

    public void setDefineName(String defineName) {
        this.defineName = defineName;
    }

    public String getComment() {
        return comment;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public Tag[] getTags() {
        return tags;
    }

    public void setTags(Tag[] tags) {
        this.tags = tags;
    }

    public String getMust(){
        return "是";
    }

    public boolean isCycle() {
        return isCycle;
    }

    public void setCycle(boolean cycle) {
        isCycle = cycle;
    }
}
