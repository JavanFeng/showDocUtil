package com.javan.showdocutil.docs.showdoc;


/**
 * showdoc实体
 * @Desc showdoc model
 * @Author Javan Feng
 * @Date 2019 06 2019/6/23 0:40
 */
public class ShowDocModel {
    /**
     * 目录
     */
    private String folder;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 是否encode
     */
    private boolean encode = true;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isEncode() {
        return encode;
    }

    public void setEncode(boolean encode) {
        this.encode = encode;
    }
}
