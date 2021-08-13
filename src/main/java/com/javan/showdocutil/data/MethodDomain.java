package com.javan.showdocutil.data;

import com.javan.showdocutil.data.base.AbstractDocument;
import com.javan.showdocutil.util.JavaDocReader;
import com.sun.javadoc.MethodDoc;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-07-27
 * @desc method info
 */
public class MethodDomain extends AbstractDocument {
    /** method*/
    private Method method;
    /** 请求信息*/
    private RequestDomain request;
    /** param*/
    private List<ParameterDomain> paramPojoList;
    /** return result*/
    private PojoDomain returnPojo;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public RequestDomain getRequest() {
        return request;
    }

    public void setRequest(RequestDomain request) {
        this.request = request;
    }

    public List<ParameterDomain> getParamPojoList() {
        return paramPojoList;
    }

    public void setParamPojoList(List<ParameterDomain> paramPojoList) {
        this.paramPojoList = paramPojoList;
    }

    public PojoDomain getReturnPojo() {
        return returnPojo;
    }

    public void setReturnPojo(PojoDomain returnPojo) {
        this.returnPojo = returnPojo;
    }


    public static MethodDomain buildMethodDomain(RequestMapping clazzMapping, Method method, MethodDoc methodDoc) throws Exception {
        final MethodDomain methodDomain = new MethodDomain();
        methodDomain.setMethod(method);
        // 方法名
        methodDomain.setDefineName(methodDoc.qualifiedName());
        // title 页面
        methodDomain.setComment(methodDoc.commentText());
        // url etc.
        final RequestDomain requestDomain = RequestDomain.buildRequestDomain(clazzMapping, method);
        // 请求参数
        final List<ParameterDomain> parameterDomainList = JavaDocReader.addRequestParam(method, methodDoc);
        // 返回值
        final PojoDomain returnPojo = JavaDocReader.addRequestReturn(methodDoc);
        methodDomain.setRequest(requestDomain);
        methodDomain.setTags(methodDoc.tags());
        methodDomain.setParamPojoList(parameterDomainList);
        methodDomain.setReturnPojo(returnPojo);
        return methodDomain;
    }

    @Override
    public List<? extends AbstractDocument> getChildren() {
        throw new RuntimeException("不支持的方法");
    }

    @Override
    public String getActualTypeName() {
        throw new RuntimeException("不支持的方法");
    }
}
