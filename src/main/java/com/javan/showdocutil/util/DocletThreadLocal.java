package com.javan.showdocutil.util;

import com.sun.javadoc.RootDoc;

/**
 * todo 可能修改为stack
 * @author fengjf
 * @version 1.0
 * @date 2021-08-02
 */
public class DocletThreadLocal {

    private static final ThreadLocal<RootDoc> DOC_ROOT_HOLDER = new ThreadLocal<>();

    public static void setDoc(RootDoc doc){
        DOC_ROOT_HOLDER.set(doc);
    }

    public static RootDoc getDoc(){
        return DOC_ROOT_HOLDER.get();
    }

    public static void remove(){
        DOC_ROOT_HOLDER.remove();
    }


}
