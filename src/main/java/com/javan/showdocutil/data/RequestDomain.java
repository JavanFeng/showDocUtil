package com.javan.showdocutil.data;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-07-27
 * @desc 暂时只存基础的
 */
public class RequestDomain {
    private RequestMapping clazzMapping;
    private RequestMapping methodMapping;
    /**
     * method request
     */
    private String requestMethod;
    /**
     * method request
     */
    private String consumes;
    /**
     * method request
     */
    private String produces;
    /**
     * 地址
     */
    private List<String> urlList;

    public RequestMapping getClazzMapping() {
        return clazzMapping;
    }

    public void setClazzMapping(RequestMapping clazzMapping) {
        this.clazzMapping = clazzMapping;
    }

    public RequestMapping getMethodMapping() {
        return methodMapping;
    }

    public void setMethodMapping(RequestMapping methodMapping) {
        this.methodMapping = methodMapping;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getConsumes() {
        return consumes;
    }

    public void setConsumes(String consumes) {
        this.consumes = consumes;
    }

    public String getProduces() {
        return produces;
    }

    public void setProduces(String produces) {
        this.produces = produces;
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }

    public static RequestDomain buildRequestDomain(RequestMapping clazzMapping, Method method) {
        final RequestMapping methodMapping = AnnotatedElementUtils.getMergedAnnotation(method, RequestMapping.class);
        RequestDomain domain = new RequestDomain();
        domain.clazzMapping = clazzMapping;
        domain.methodMapping = methodMapping;
        RequestMethod[] methods = methodMapping.method();
        // uri suffix and method
        String[] uriSuffixArray;
        if (methods.length == 0) {
            // is requestMapping default get and post
            domain.setRequestMethod("GET,POST");
            uriSuffixArray = methodMapping.value();
        } else {
            String requestMethod = Arrays.stream(methods).map(RequestMethod::name).collect(Collectors.joining(","));
            domain.setRequestMethod(requestMethod);
            uriSuffixArray = methodMapping.value();
        }
        // prefix
        final String[] prefixArray = clazzMapping.value();
        domain.setUrlList(buildUrls(prefixArray,uriSuffixArray));
        domain.setConsumes(String.join(",", methodMapping.consumes()));
        domain.setProduces(String.join(",", methodMapping.produces()));
        if(domain.getConsumes().length() == 0){
            domain.setConsumes(null);
        }
        if(domain.getProduces().length() == 0){
            domain.setProduces(null);
        }
        return domain;
    }

    private static List<String> buildUrls(String[] prefixArray, String[] uriSuffixArray) {
        if(prefixArray ==null || prefixArray.length == 0){
            return Arrays.asList(uriSuffixArray);
        }else{
            return Arrays.stream(prefixArray)
                    .map(pre-> Arrays.stream(uriSuffixArray).map(e->pre+e).collect(Collectors.toList()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
    }

}
