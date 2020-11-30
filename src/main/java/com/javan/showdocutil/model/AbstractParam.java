package com.javan.showdocutil.model;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fengjf
 * @version 1.0
 * @date 2020-11-23
 * @desc 参数
 */
public abstract class AbstractParam implements IParam {
    /**
     * 参数字段
     */
    public List<ParamFieldInfo> children = new ArrayList<>();

    @Override
    public IParam getIparamByName(String typeName) {
        return findParamByName(children, typeName);
    }

    private ParamFieldInfo findParamByName(List<ParamFieldInfo> children,
                                           String typeName) {
        if (children == null) {
            return null;
        }
        for (ParamFieldInfo child : children) {
            // TODO: 区分泛型下实体的类型等
            if (child instanceof TypeVariableParamFieldInfo || child instanceof PlaceHolderParamFieldInfo) {
                if (child instanceof TypeVariableParamFieldInfo) {
                    if (((TypeVariableParamFieldInfo) child).getRawClazz().getTypeName().equals(typeName)) {
                        return child;
                    }
                }
                if (child instanceof PlaceHolderParamFieldInfo) {
                    if (((PlaceHolderParamFieldInfo) child).getClazz().getTypeName().equals(typeName)) {
                        return child;
                    }
                }

                //
                List<ParamFieldInfo> typeChild = child.getChildren();
                ParamFieldInfo paramByName = findParamByName(typeChild, typeName);
                if (paramByName != null) {
                    return paramByName;
                }
            }

            if (child.getClass().equals(ParamFieldInfo.class)) {
                Field type = child.getParameter();
                String name = type.getDeclaringClass().getName();
                String fullName = name + "." + type.getName();
                if (type.getName().equals(typeName) || fullName.equals(typeName)) {
                    return child;
                } else {
                    ParamFieldInfo paramByName = findParamByName(child.getChildren(), typeName);
                    if (paramByName != null) {
                        return paramByName;
                    }
                }
            }
        }
        return null;
    }
}
