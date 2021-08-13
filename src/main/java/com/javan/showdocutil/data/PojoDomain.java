package com.javan.showdocutil.data;

import com.javan.showdocutil.data.base.AbstractDocument;
import com.javan.showdocutil.util.BaseTypeUtil;
import com.javan.showdocutil.docs.showdoc.ControllerShowDocUtils;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-07-27
 */
public class PojoDomain extends AbstractDocument {

    private Class<?> clazz;

    private Type type;
    /** PojoDomain, fileDomain*/
    private List<? extends AbstractDocument> fields;

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<? extends AbstractDocument> getFields() {
        return fields;
    }

    @Override
    public List<? extends AbstractDocument> getChildren() {
        return fields;
    }

    public void setFields(List<? extends AbstractDocument> fields) {
        this.fields = fields;
    }

    public static PojoDomain buildBasePojoDomain(Type type) throws ClassNotFoundException {
        final String className = type.qualifiedTypeName();
        final Class<?> aClass = ControllerShowDocUtils.CLASS_LOAD.loadClass(className);
        PojoDomain domain = new PojoDomain();
        domain.setClazz(aClass);
        domain.setDefineName(type.simpleTypeName());
        domain.setComment(type.simpleTypeName());
        domain.setFields(Collections.emptyList());
        domain.setType(type);
        return domain;
    }

    public static PojoDomain buildPojoDomain(Class<?> clazz, ClassDoc aClass) {
        PojoDomain domain = new PojoDomain();
        domain.setClazz(clazz);
        domain.setComment(aClass.commentText());
        domain.setDefineName(aClass.qualifiedTypeName());
        domain.setTags(aClass.tags());
        domain.setType(aClass);
        // todo fieldList if base?
        return domain;
    }

    public static PojoDomain buildPojoDomainReturn(MethodDoc methodDoc) throws ClassNotFoundException {
        PojoDomain domain = new PojoDomain();
        final String className = methodDoc.returnType().qualifiedTypeName();
        if("void".equals(className)){
            domain.setClazz(Void.class);
        }else if(methodDoc.returnType().isPrimitive()){
            domain.setClazz(BaseTypeUtil.getPrimaryTypeClass(className));
        }else {
            final Class<?> clazz = ControllerShowDocUtils.CLASS_LOAD.loadClass(className);
            domain.setClazz(clazz);
        }
        String comment = "结果";
        Tag[] returns = methodDoc.tags("return");
        if (returns != null && returns.length != 0) {
            comment = Arrays.stream(returns).map(Tag::text).collect(Collectors.joining(","));
        }
        domain.setComment(comment);
        domain.setDefineName("result");
        domain.setTags(methodDoc.tags());
        // todo fieldList if base?
        return domain;
    }

    @Override
    public String getMust() {
        boolean paramConstraint = clazz.getAnnotation(Valid.class) != null || clazz.getAnnotation(Validated.class) != null;
        if (paramConstraint) {
            return "是";
        } else {
            return "否";
        }
    }

    @Override
    public String getActualTypeName() {
        return clazz.getTypeName();
    }
}
