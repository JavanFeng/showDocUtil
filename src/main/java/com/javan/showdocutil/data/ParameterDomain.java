package com.javan.showdocutil.data;

import com.javan.showdocutil.data.base.AbstractDocument;
import com.javan.showdocutil.util.BaseTypeUtil;
import com.javan.showdocutil.util.JavaDocReader;
import com.sun.javadoc.ParamTag;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Constraint;
import javax.validation.Valid;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-07-27
 * @desc 方法参数
 */
public class ParameterDomain extends AbstractDocument {

    private Parameter parameter;

    private List<AbstractDocument> fieldDomainList;

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public List<AbstractDocument> getFieldDomainList() {
        return fieldDomainList;
    }

    public void setFieldDomainList(List<AbstractDocument> fieldDomainList) {
        this.fieldDomainList = fieldDomainList;
    }

    public static ParameterDomain buildParameterDomain(Parameter parameter, com.sun.javadoc.Parameter parameterDoc, ParamTag paramTag) throws Exception {
        ParameterDomain domain = new ParameterDomain();
        domain.setParameter(parameter);
        domain.setComment(paramTag.parameterComment());
        domain.setDefineName(paramTag.parameterName());
        if (!BaseTypeUtil.isBaseType(parameterDoc.type().qualifiedTypeName())) {
            domain.setFieldDomainList(JavaDocReader.getParamText(parameterDoc));
        }
        return domain;
    }

    @Override
    public List<? extends AbstractDocument> getChildren() {
        return fieldDomainList;
    }

    @Override
    public String getMust() {
        boolean paramConstraint = parameter.getAnnotation(Valid.class) != null || parameter.getAnnotation(Validated.class) != null;
        if (paramConstraint) {
            return "是";
        } else {
            paramConstraint = AnnotationUtils.getAnnotation(parameter, Constraint.class) != null;
            if (paramConstraint) {
                return "是";
            } else {
                return "否";
            }
        }
    }

    @Override
    public String getActualTypeName() {
        return parameter.getType().getTypeName();
    }
}
