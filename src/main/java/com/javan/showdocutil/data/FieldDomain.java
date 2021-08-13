package com.javan.showdocutil.data;

import com.javan.showdocutil.data.base.AbstractDocument;
import com.javan.showdocutil.util.JavaDocReader;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.Type;
import org.springframework.core.annotation.AnnotationUtils;

import javax.validation.Constraint;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-07-27
 * @desc field info
 */
public class FieldDomain extends AbstractDocument {

    private Field field;

    private Type realType;

    private String genericTypeName;

    private List<? extends AbstractDocument> children;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public List<? extends AbstractDocument> getChildren() {
        return children;
    }

    public Type getRealType() {
        return realType;
    }

    public void setRealType(Type realType) {
        this.realType = realType;
    }

    public String getGenericTypeName() {
        return genericTypeName;
    }

    public void setGenericTypeName(String genericTypeName) {
        this.genericTypeName = genericTypeName;
    }

    public void setChildren(List<? extends AbstractDocument> children) {
        this.children = children;
    }

    public static FieldDomain buildFieldDomain(Field field, FieldDoc doc, Type type,
                                               String genericTypeName,
                                               Map<String, Type> genericTypeAndTypeName) throws Exception {
        if (!doc.type().equals(type)) {
            // 泛型
            FieldDomain domain = new FieldDomain();
            domain.setField(field);
            domain.setRealType(type);
            domain.setGenericTypeName(genericTypeName);
            domain.setDefineName(doc.name());
            domain.setComment(doc.commentText());
            domain.setTags(doc.tags());
            // user
            final List<AbstractDocument> pojoDomainByDocType = JavaDocReader.getPojoDomainByDocType(type,genericTypeAndTypeName);
            if(pojoDomainByDocType.size() == 1){
                final AbstractDocument child = pojoDomainByDocType.get(0);
                domain.setChildren(child.getChildren());
                domain.setCycle(child.isCycle());
                domain.setComment(domain.getComment()+"("+child.getComment()+")");
            }else {
                domain.setChildren(pojoDomainByDocType);
            }
            return domain;
        }else {
            FieldDomain domain = new FieldDomain();
            domain.setField(field);
            domain.setRealType(type);
            domain.setDefineName(doc.name());
            domain.setComment(doc.commentText());
            domain.setTags(doc.tags());
            domain.setChildren(JavaDocReader.getPojoDomainByDocType(type, Collections.emptyMap()));
            return domain;
        }
    }

    @Override
    public String getMust() {
        final boolean must = AnnotationUtils.getAnnotation(field, Constraint.class) != null;
        if (must) {
            return "是";
        } else {
            return "否";
        }
    }

    @Override
    public String getActualTypeName() {
        return realType.qualifiedTypeName();
    }
    public String getActualFullTypeName() {
        return realType.toString();
    }

}
