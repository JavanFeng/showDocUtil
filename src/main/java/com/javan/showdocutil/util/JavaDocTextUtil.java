package com.javan.showdocutil.util;

import cn.hutool.core.util.StrUtil;
import com.javan.showdocutil.enums.ENParamType;
import com.javan.showdocutil.model.IParam;
import com.javan.showdocutil.model.MethodInfo;
import com.javan.showdocutil.model.MethodInfoThreadLocal;
import com.javan.showdocutil.model.ParamFieldInfo;
import com.sun.javadoc.*;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.util.*;

/**
 * @Desc TODO
 * @Author Javan Feng
 * @Date 2019 06 2019/6/25 21:37
 */
class JavaDocTextUtil {

    private static final String SERIAL_VERSION_UID = "serialVersionUID";

    private static RootDoc root;

    // 一个简单Doclet,收到 RootDoc对象保存起来供后续使用
    // 参见参考资料6
    public static class Doclet {

        public Doclet() {
        }

        public static boolean start(RootDoc root) {
            JavaDocTextUtil.root = root;
            return true;
        }


        public static LanguageVersion languageVersion() {
            return LanguageVersion.JAVA_1_5;
        }
    }

    // 获取实体类
    // https://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#sourcepath
    public static String getModelText(String typeName, String prefix, IParam iParam,ENParamType paramType) throws Exception {
        return getModelText(typeName, null, prefix, iParam,paramType);
    }

    // 获取实体类
    // https://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#sourcepath
    private static String getModelText(String typeName, StringBuilder build, String prefix, IParam paramByName, ENParamType paramType) throws Exception {
        if (paramByName==null){
            return "";
        }
        Class<?> paramterClass = paramByName.getParamterClass();
        if ("java.lang.reflect.Field".equals(paramterClass.getName())){
            java.lang.reflect.Type type = ((ParamFieldInfo) paramByName).getParameter().getGenericType();
            try {
                if (type instanceof ParameterizedTypeImpl){
                    paramterClass = Class.forName(((ParameterizedTypeImpl) type).getActualTypeArguments()[0].getTypeName());
                }else{
                    paramterClass = Class.forName(type.getTypeName());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        // classPatch
        String s = paramterClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        String classPath = s.substring(1, s.length() - 1);
        // package 2 path
        String workplace = classPath.replace("/target/classes", "");
        String path = paramterClass.getTypeName().replace(".", "/");
        com.sun.tools.javadoc.Main.execute(new String[]{"-doclet",
                JavaDocTextUtil.Doclet.class.getName(),
                "-quiet",
                "-private",
                "-Xmaxerrs", "1",
                "-encoding", "utf-8",
                /*  "-classpath",
                  classPath,*/
// 获取单个代码文件FaceLogDefinition.java的javadoc
                workplace + "/src/main/java/" + path + ".java"});
        return doGetModelText(typeName, build, prefix, paramByName,paramType);
    }

    private static String doGetModelText(String typeName, StringBuilder builder, String prefix, IParam paramByName,ENParamType paramType) throws Exception {
        if (builder == null) {
            builder = new StringBuilder();
        }
        ClassDoc[] classes = root.classes();
        // 只会存在一个
        ClassDoc aClass = classes[0];
        FieldDoc[] fields = aClass.fields();
        int rowId = 0;
        List<Map<String,String>> mapList = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            FieldDoc field = fields[i];
            String name = field.name();
            if (SERIAL_VERSION_UID.equals(name)){
                continue;
            }
            rowId++;
            String comment = field.commentText();
            Type type = field.type();
            String ty = field.type().qualifiedTypeName();
            String fieldFullName = field.qualifiedName();
            // 基础
            // base type
            builder.append("| ");
            builder.append(rowId);
            builder.append(" | ");
            builder.append(name);
            builder.append(" | ");
            builder.append(comment);
            builder.append(" | ");
            builder.append(BaseTypeUtil.getActualTypeName(type));
            builder.append(" | ");
            IParam iparamByName = paramByName.getIparamByName(fieldFullName);
            String requiredName = "否";
            //返回对象直接默认是
            if (ENParamType.RESP == paramType){
                requiredName = "是";
            }else if (iparamByName != null&&iparamByName.getRequired() != null&& Boolean.TRUE.equals(iparamByName.getRequired())){
                requiredName = "是";
            }
            builder.append(requiredName);
            builder.append(" | |");
            if (i== fields.length-1){
                //前一个表单闭合
                builder.append("|");
            }
            builder.append("\r\n");
            if (!BaseTypeUtil.isBaseType(ty)||"java.util.List".equals(ty)) {
                generateExtContent(mapList,type,paramByName,paramType);
            }
        }
        for (Map<String, String> extMap : mapList) {
            builder.append(GenerateFactory.generateExtContent(extMap));
        }
        return builder.toString();
    }

    private static void generateExtContent(List<Map<String, String>> mapList, Type type, IParam paramByName, ENParamType paramType) throws Exception {
        StringBuilder ext = new StringBuilder();
        // 不是基础类型 ，需要再添加额外的字段
        ParameterizedType parameterizedType = type.asParameterizedType();
        IParam fieldParam = paramByName.getIparamByName(StrUtil.lowerFirst(type.simpleTypeName()));
        if (parameterizedType != null) {
            if (BaseTypeUtil.isBaseType(parameterizedType.typeArguments()[0].qualifiedTypeName())){
                //类似List<String>这边不做处理
                return;
            }
            // 有泛型
            JavaDocReader.doParseType(new Type[]{type}, ext, "", true, fieldParam, !"java.util.List".equals(type.qualifiedTypeName()),paramType);
        } else {
            JavaDocReader.doParseType(new Type[]{type}, ext, "", false, fieldParam, false,paramType);
        }
        if (StrUtil.isBlank(ext)) {
            return;
        }
        Map<String,String> map = new HashMap<>();
        map.put("paramTypeName",paramType.getLabel());
        map.put("extParam",ext.toString());
        map.put("typeName",StrUtil.lowerFirst(type.simpleTypeName()));
        mapList.add(map);
    }
    private static Type[] getTypes(Type type) {
        // 泛型
        Type[] types;
        ParameterizedType parameterizedType = type.asParameterizedType();
        if (parameterizedType != null) {
            types = parameterizedType.typeArguments();
        } else {
            types = new Type[1];
            types = Arrays.asList(type).toArray(types);
        }
        return types;
    }
}
