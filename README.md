# showDocUtil（旧） 之后补充
spring环境下使用javadoc来生成文档


#说明：
1. 使用方法：
 - 将其打包，maven引入，获取直接代码copy;
 - 调用其方法：
 ShowDocWorkUtil.getInstance().withConsolePrint()
 .withUpdateShowDoc(domain, app_key,app_token).doWork(xxx);
 
 withConsolePrint：控制打印生成内容
 withUpdateShowDoc：更新内容到showdoc文档中
 domain: 目标地址，一般是www.showdoc.cc 或者为部署的showdoc地址
 app_key,app_token: 项目的开放api信息
 
 
2. 注意点：
 - 不支持对象的循环引用
 - 所有的参数只能是classpath的，暂不支持jar包中的实体
 - 对于集合类，只支持map hashMap linkedHashMap list arraylist linkedList collection  set 和 hashSet
 - 对于参数的的是否必选不支持，默认都是y.
 
3. TODO:
   - 参数是否必选，根据是否带有@Constraint注解。
   - 参数支持jar包中的对象解析。
   
