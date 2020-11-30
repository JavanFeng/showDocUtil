package com.javan.showdocutil.model;

import java.lang.reflect.Method;
import java.util.List;

/**
 * method
 */
public class MethodInfo {

    private String requestMethod;

    private String[] urisSuffix;

    private String[] uriPrefix;

    private Class<?> sourceClass;

    private Method method;

    private List<MethodParamInfo> params;

    private MethodReturnInfo returnInfo;

    public MethodReturnInfo getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(MethodReturnInfo returnInfo) {
        this.returnInfo = returnInfo;
    }

    public List<MethodParamInfo> getParams() {
        return params;
    }

    public void setParams(List<MethodParamInfo> params) {
        this.params = params;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String[] getUrisSuffix() {
        return urisSuffix;
    }

    public void setUrisSuffix(String[] urisSuffix) {
        this.urisSuffix = urisSuffix;
    }

    public Class<?> getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(Class<?> sourceClass) {
        this.sourceClass = sourceClass;
    }

    public String[] getUriPrefix() {
        return uriPrefix;
    }

    public void setUriPrefix(String[] uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    public IParam getParamByName(String typeName) {
        if (params == null) {
            return null;
        }
        // find param
        for (MethodParamInfo param : params) {
            if (param.getParameter().getType().getSimpleName().equals(typeName)) {
                return param;
            } else {
                ParamFieldInfo paramByName = findParamByName(param.getChildren(), typeName);
                if (paramByName != null) {
                    return paramByName;
                }
            }
        }
        return null;
    }

    private ParamFieldInfo findParamByName(List<ParamFieldInfo> children,
                                           String typeName) {
        if (children == null) {
            return null;
        }
        for (ParamFieldInfo child : children) {
            if (child.getParameter().getType().getSimpleName().equals(typeName)) {
                return child;
            } else {
                ParamFieldInfo paramByName = findParamByName(child.getChildren(), typeName);
                if (paramByName != null) {
                    return paramByName;
                }
            }
        }
        return null;
    }
}
