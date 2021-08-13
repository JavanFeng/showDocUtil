package com.javan.showdocutil.data;

import com.javan.showdocutil.data.base.AbstractDocument;
import com.javan.showdocutil.model.ControllerClassInfo;
import com.javan.showdocutil.util.JavaDocReader;
import com.sun.javadoc.ClassDoc;

import java.util.List;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-07-27
 * @desc controller class info
 */
public class ControllerClazzAggre extends AbstractDocument {
    /** class*/
    private Class<?> clazz;
    /** method */
    private List<MethodDomain> methodList;

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public List<MethodDomain> getMethodList() {
        return methodList;
    }

    public void setMethodList(List<MethodDomain> methodList) {
        this.methodList = methodList;
    }

    public static ControllerClazzAggre buildControllerClazzAggre(ControllerClassInfo methodInfo, ClassDoc aClass) throws Exception {
        Class<?> clazz = methodInfo.getControllerClazz();
        List<String> methodNameList = methodInfo.getMethodNameList();
        ControllerClazzAggre controllerClazzAggre = new ControllerClazzAggre();
        controllerClazzAggre.setClazz(clazz);
        controllerClazzAggre.setComment(aClass.commentText());
        controllerClazzAggre.setDefineName(aClass.qualifiedTypeName());
        controllerClazzAggre.setTags(aClass.tags());
        controllerClazzAggre.setMethodList(JavaDocReader.buildMethodDomainList(methodNameList,clazz,aClass));
        return controllerClazzAggre;
    }

    @Override
    public List<? extends AbstractDocument> getChildren() {
        return methodList;
    }

    @Override
    public String getActualTypeName() {
        return clazz.getTypeName();
    }
}
