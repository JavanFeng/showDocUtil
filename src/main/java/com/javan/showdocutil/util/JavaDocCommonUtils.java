package com.javan.showdocutil.util;

import com.javan.showdocutil.constant.PrefixMark;
import com.javan.showdocutil.data.*;
import com.javan.showdocutil.data.base.AbstractDocument;
import com.javan.showdocutil.docs.showdoc.ShowDocConfiguration;
import com.javan.showdocutil.docs.showdoc.ShowReqRespModel;
import com.sun.javadoc.Type;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-08-03
 */
public class JavaDocCommonUtils {
    /**
     * 去除html
     */
    public static String delHtmlTag(String str) {
        String newStr = "";
        newStr = str.replaceAll("<[.[^>]]*>", "");
        newStr = newStr.replaceAll(" ", "");
        return newStr;
    }

    /**
     * 目录 以 / 隔开 showdoc中为目录
     */
    public static String getFolder(ControllerClazzAggre className) {
        return delHtmlTag(className.getComment());
    }

    /**
     * 标题
     */
    public static String getTitle(MethodDomain methodDomain) {
        return delHtmlTag(methodDomain.getComment());
    }

    public static List<ShowReqRespModel> getRequestParam(MethodDomain methodDomain) {
        List<ParameterDomain> paramPojoList = methodDomain.getParamPojoList();
        List<ShowReqRespModel> list = new ArrayList<>();
        for (ParameterDomain domain : paramPojoList) {
            list.addAll(doExecuteHandleBuildShowReqAndResp(domain,true));
        }
        return list;
    }

    private static List<ShowReqRespModel> buildChildren(String parentDefineName,
                                                        List<? extends AbstractDocument> fieldDomainList,
                                                        List<ShowReqRespModel> list,
                                                        String namePrefix) {
        if (fieldDomainList == null) {
            return list;
        }
        for (AbstractDocument domain : fieldDomainList) {
            if (!CustomTagForReqNameUtils.isIgnore(domain.getTags())) {
                ShowReqRespModel model = new ShowReqRespModel();
                model.setName(ModelValueUtils.getPrefixStyleValue(namePrefix, CustomTagForReqNameUtils.handleName(namePrefix + getDefineNamePrefixString(parentDefineName) + domain.getDefineName(), domain.getTags())));
                model.setDescription(domain.getComment());
                model.setMust(domain.getMust());
                executeHandlesDomains(parentDefineName, list, domain, model,false);
            }
        }
        return list;
    }

    private static void executeHandlesDomains(String parentDefineName, List<ShowReqRespModel> list,
                                              AbstractDocument domain, ShowReqRespModel model, boolean alwaysAdd) {
        if (domain instanceof PojoDomain) {
            doHandlePojoDomain(parentDefineName, list, domain, model, alwaysAdd);
        } else if (domain instanceof FieldDomain) {
            doHandleFieldDomain(parentDefineName, list, domain, model, alwaysAdd);
        } else if (domain instanceof ParameterDomain) {
            doHandleParameterDomain(parentDefineName, list, domain, model, alwaysAdd);
        } else {
            // ignore
        }
    }

    private static void doHandleFieldDomain(String parentDefineName, List<ShowReqRespModel> list, AbstractDocument domain,
                                            ShowReqRespModel model, boolean alwaysAdd) {
        final Field field = ((FieldDomain) domain).getField();
        final java.lang.reflect.Type type = field.getGenericType();
        model.setType(domain.getActualTypeName());
        doHandleDomain(parentDefineName, list, domain, model, BaseTypeUtil.isBaseListGeneric(type), BaseTypeUtil.getGenericTypeValues(type), alwaysAdd);
    }

    /**
     * @param baseListGeneric   是否为集合 且泛型为基础
     * @param genericTypeValues 泛型内容
     * @param alwaysAdd         总是添加model到list中
     */
    private static void doHandleDomain(String parentDefineName,
                                       List<ShowReqRespModel> list,
                                       AbstractDocument domain,
                                       ShowReqRespModel model,
                                       boolean baseListGeneric,
                                       String genericTypeValues,
                                       boolean alwaysAdd) {
        if (BaseTypeUtil.isCollectionType(domain.getActualTypeName())) {
            list.add(model);
            if (baseListGeneric) {
                model.setRemark("集合内部数据类型：" + genericTypeValues);
            } else {
                // 非基础类型
                model.setRemark("集合内部数据类型：" + genericTypeValues);
                buildChildren(parentDefineName, domain.getChildren(), list, PrefixMark.PREFIX);
            }
        } else {
            // 只有基础类型，和集合类型需要加入到参数列表中。
            if (isBaseOrCollectionOrCycleModel(domain) || alwaysAdd) {
                list.add(model);
            }
            buildChildren(getDefineNamePrefixString(parentDefineName) + domain.getDefineName(), domain.getChildren(), list, "");
        }
    }

    private static void doHandlePojoDomain(String parentDefineName, List<ShowReqRespModel> list,
                                           AbstractDocument domain, ShowReqRespModel model, boolean alwaysAdd) {
        model.setType(domain.getActualTypeName());
        final Type type = ((PojoDomain) domain).getType();
        doHandleDomain(parentDefineName, list, domain, model,
                BaseTypeUtil.isBaseListGeneric(type), BaseTypeUtil.getGenericTypeValues(type), alwaysAdd);
    }

    private static void doHandleParameterDomain(String parentDefineName, List<ShowReqRespModel> list,
                                                AbstractDocument domain, ShowReqRespModel model, boolean alwaysAdd) {
        model.setType(domain.getActualTypeName());
        final java.lang.reflect.Type type = ((ParameterDomain) domain).getParameter().getParameterizedType();
        doHandleDomain(parentDefineName, list, domain, model, BaseTypeUtil.isBaseListGeneric(type), BaseTypeUtil.getGenericTypeValues(type), alwaysAdd);

    }

    private static boolean isBaseOrCollectionOrCycleModel(AbstractDocument domain) {
        return BaseTypeUtil.isBaseType(domain.getActualTypeName())
                || BaseTypeUtil.isCollectionType(domain.getActualTypeName())
                // 循环实体,非基础集合类型，但是不存在子类，说明是循环实体不进行解析，也需要存储告诉一下前端有这个实体
                || domain.isCycle();
    }

    private static String getDefineNamePrefixString(String parentDefineName) {
        if (parentDefineName == null || parentDefineName.trim().length() == 0) {
            return "";
        }
        return parentDefineName + ".";
    }


    public static List<List<ShowReqRespModel>> getReturn(MethodDomain methodDomain, ShowDocConfiguration baseConfiguration) {
        final PojoDomain domain = methodDomain.getReturnPojo();

        final Class<?> commonResultClazz = baseConfiguration.getCommonResultClazz();
        final String commonResultTypeName = baseConfiguration.getCommonResultTypeName();
        final Class<?> clazz = domain.getClazz();
        AbstractDocument returnModel = domain;
        PojoDomain commonModel = null;
        ShowReqRespModel genericModel=null;
        boolean addFirst = true;
        if(clazz.equals(commonResultClazz) && commonResultTypeName !=null){
            // 业务参数
            returnModel = domain.getChildren().stream().filter(e->e instanceof FieldDomain)
                        .filter(e->((FieldDomain) e).getGenericTypeName()!= null)
                        .findFirst().orElseThrow(()->new RuntimeException("未找到指定的泛型实体"));
            genericModel = new ShowReqRespModel();
            genericModel.setName(ModelValueUtils.getMarkdownItalicParamValue(returnModel.getDefineName()));
            genericModel.setDescription(returnModel.getComment());
            genericModel.setType(((FieldDomain) returnModel).getActualFullTypeName());
            genericModel.setMust(returnModel.getMust());
            genericModel.setRemark("业务参数实体");
            // 通用实体
            final List<? extends AbstractDocument> commonChild = domain.getChildren().stream().filter(e -> e instanceof FieldDomain)
                    .filter(e -> ((FieldDomain) e).getGenericTypeName() == null).collect(Collectors.toList());
            commonModel = new PojoDomain();
            BeanUtils.copyProperties(domain,commonModel);
            commonModel.setFields(commonChild);
            addFirst = false;
        }

        final List<ShowReqRespModel> model = doExecuteHandleBuildShowReqAndResp(returnModel,addFirst);
        final List<ShowReqRespModel> common = doExecuteHandleBuildShowReqAndResp(commonModel,addFirst);
        if(genericModel != null){
            common.add(genericModel);
        }
        return Arrays.asList(model,common);
    }

    private static List<ShowReqRespModel> doExecuteHandleBuildShowReqAndResp(AbstractDocument domain,boolean addFirst) {
        if(domain == null){
            return Collections.emptyList();
        }
        List<ShowReqRespModel> list = new ArrayList<>();
        if (!CustomTagForReqNameUtils.isIgnore(domain.getTags())) {
            ShowReqRespModel model = new ShowReqRespModel();
            model.setType(domain.getActualTypeName());
            model.setMust(domain.getMust());
            if (!isBaseOrCollectionOrCycleModel(domain) || !addFirst) {
                model.setDescription(domain.getComment());
                model.setRemark(CustomTagForReqNameUtils.handleName("参数实体(" + domain.getComment() + ")", domain.getTags()));
                model.setName(ModelValueUtils.getMarkdownItalicParamValue(CustomTagForReqNameUtils.handleName(domain.getDefineName(), domain.getTags())));
                if(addFirst) {
                    list.add(model);
                }
                buildChildren(null, domain.getChildren(), list, "");
            } else {
                model.setDescription(CustomTagForReqNameUtils.handleName(domain.getComment(), domain.getTags()));
                model.setName(CustomTagForReqNameUtils.handleName(domain.getDefineName(), domain.getTags()));
                executeHandlesDomains(null,list,domain,model,true);
            }
        }
        return list;
    }

    public static void simplifyType(List<ShowReqRespModel> requestParam) {
        if(requestParam!=null){
            requestParam.stream().forEach(e->{
                final String type = e.getType();
                if(!type.endsWith(">")){
                    final String[] split = type.split("\\.");
                    e.setType(split[split.length-1]);
                }
            });
        }
    }
}
