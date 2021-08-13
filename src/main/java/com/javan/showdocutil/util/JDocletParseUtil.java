package com.javan.showdocutil.util;

import com.javan.showdocutil.test.UserController;
import com.sun.javadoc.*;

/**
 * @author fengjf
 * @version 1.0
 * @date 2021-08-02
 * @desc TODO
 */
public class JDocletParseUtil {

    private volatile static boolean inited;

    public volatile static Type OBJECT;

    // 一个简单Doclet,收到 RootDoc对象保存起来供后续使用
    // 参见参考资料6
    public static class JDoclet {

        public JDoclet() {
        }

        public static boolean start(RootDoc root) {
            DocletThreadLocal.setDoc(root);
            return true;
        }

        public static LanguageVersion languageVersion() {
            return LanguageVersion.JAVA_1_5;
        }
    }


    public static RootDoc parseDoc(Class<?> sourceClass){
        // classPatch
        String s = sourceClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        String classPath = s.substring(1, s.length() - 1);
        String workplace = classPath.replace("/target/classes", "");
        workplace = workplace.replace("/", "\\");
        // package 2 path
        String path = sourceClass.getTypeName().replace(".", "\\");
        com.sun.tools.javadoc.Main.execute(new String[]{"-doclet",
                JDoclet.class.getName(),
                "-quiet",
                "-private",
                "-Xmaxerrs", "0",
                "-encoding", "utf-8",
                /* "-classpath",
                 classPath + sourceClass.getTypeName(),*/
                // 获取单个代码文件FaceLogDefinition.java的javadoc
                workplace + "\\src\\main\\java\\" + path + ".java"});
        try {
            final RootDoc doc = DocletThreadLocal.getDoc();
            if(!inited){
                doInitObjDoc(doc);
            }
            return doc;
        } finally {
            DocletThreadLocal.remove();
        }
    }

    private static void doInitObjDoc(RootDoc doc) {
        if(OBJECT != null){
            return;
        }
        final ClassDoc aClass = doc.classes()[0];
        Type type = aClass.superclassType();
        if(type != null){
            while(type != null){
                final ClassDoc classDoc = type.asClassDoc();
                type = type.asClassDoc().superclassType();
                if(type == null){
                    OBJECT = classDoc;
                }
            }
        }else{
            OBJECT = aClass;
        }
        inited = true;
    }


    // https://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#sourcepath
    public static void main(final String... args) throws Exception {
        // 调用com.sun.tools.javadoc.Main执行javadoc,参见 参考资料3
        // javadoc的调用参数，参见 参考资料1
        // -doclet 指定自己的docLet类名
        // -classpath 参数指定 源码文件及依赖库的class位置，不提供也可以执行，但无法获取到完整的注释信息(比如annotation)
        // -encoding 指定源码文件的编码格式
        com.sun.tools.javadoc.Main.execute(new String[]{"-doclet",
                Doclet.class.getName(),
// 因为自定义的Doclet类并不在外部jar中，就在当前类中，所以这里不需要指定-docletpath 参数，
//				"-docletpath",
//				Doclet.class.getResource("/").getPath(),
                "-quiet",
                "-private",
                "-Xmaxerrs", "1",
                "-encoding", "utf-8",
                "-classpath",
                "D:\\workspace\\hello-world-spring-boot\\target\\classes",
// 获取单个代码文件FaceLogDefinition.java的javadoc
                "D:\\workspace\\hello-world-spring-boot\\src\\main\\java\\com\\study\\h\\controller\\HelloController2.java"});
        //"D:\\workspace\\hello-world-spring-boot\\src\\main\\java\\com\\study\\h\\controller\\GreanStanardProjectDTO.java"});
//        doParse(null, null);
    }
}
