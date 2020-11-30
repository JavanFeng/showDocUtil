package com.javan.showdocutil.util;

import com.javan.showdocutil.model.IParam;
import com.javan.showdocutil.model.MethodInfo;
import com.javan.showdocutil.model.MethodInfoThreadLocal;
import com.sun.javadoc.*;

import java.util.Arrays;

/**
 * @Desc TODO
 * @Author Javan Feng
 * @Date 2019 06 2019/6/25 21:37
 */
class JavaDocTextUtil {
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
    public static String getModelText(String typeName, String prefix, IParam iParam) throws Exception {
        return getModelText(typeName, null, prefix, iParam);
    }

    // 获取实体类
    // https://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#sourcepath
    private static String getModelText(String typeName, StringBuilder build, String prefix, IParam paramByName) throws Exception {

        Class<?> paramterClass = paramByName.getParamterClass();
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
        //"D:\\workspace\\hello-world-spring-boot\\src\\main\\java\\com\\study\\h\\controller\\GreanStanardProjectDTO.java"});
        return doGetModelText(typeName, build, prefix, paramByName);
    }

    private static String doGetModelText(String typeName, StringBuilder builder, String prefix, IParam paramByName) throws Exception {
        if (builder == null) {
            builder = new StringBuilder();
        }
        ClassDoc[] classes = root.classes();
        // 只会存在一个
        ClassDoc aClass = classes[0];
        FieldDoc[] fields = aClass.fields();
        for (FieldDoc field : fields) {
            String name = field.name();
            String comment = field.commentText();
            Type type = field.type();
            String ty = field.type().qualifiedTypeName();
            String fieldFullName = field.qualifiedName();
            // 基础
            // base type
            builder.append("|");
            builder.append(prefix);
            builder.append(name);
            builder.append("|");
            IParam iparamByName = paramByName.getIparamByName(fieldFullName);
            builder.append(iparamByName == null ? "" : (iparamByName.getRequired() == null) ? "" : iparamByName.getRequired().toString());
            builder.append("|");
            builder.append(BaseTypeUtil.getActualTypeName(type));
            builder.append("|");
            builder.append(comment);
            builder.append("|");
            builder.append("\r\n");
            if (!BaseTypeUtil.isBaseType(ty)) {
                // 不是基础类型 ，需要再添加额外的字段
                ParameterizedType parameterizedType = type.asParameterizedType();
                boolean flag = false;
                IParam fieldParam = paramByName.getIparamByName(type.simpleTypeName());
                if (parameterizedType != null) {
                    // 有汎型
                    flag = true;
                    JavaDocReader.doParseType(new Type[]{type}, builder, prefix, flag, fieldParam, true);
                } else {
                    JavaDocReader.doParseType(new Type[]{type}, builder, prefix + PrefixMark.PREFIX, flag, fieldParam, false);
                }
            }
        }
        return builder.toString();
    }

    private static Type[] getTypes(Type type) {
        // 泛型
        Type[] types = null;
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
