# KXml
介绍
=============
	xml与对象的互转，支持自定义的类，注解后，随便混淆，支持忽略某些类
	数据类型：List，数组，Map，基本数据类型，enum。
	注：
		1.没有注解的字段不会处理
		2.构造方法，最好添加一个无参构造方法（特别存在有参构造方法）
		3.不支持根元素就是List，Map
		4.如果不需要改变子元素的名字XmlElementList，XmlElementMap可以用XmlElement替代
		5.只要你根据类型配置XmlStringAdapter<T>对象，那么该类型可以随意从字符串与对象互转，比如数字显示为16进制
		
	注解：
	XmlAttribute	xml属性注解（基本数据类型，enum）
	XmlElement	    xml元素注解（自定义类型，数组，基本数据类型，enum）
	XmlElementList  xml的list元素注解，如果需要指定特殊类： item：item的类
	XmlElementMap	xml的map元素注解，如果需要指定特殊类：keyType：key的类，valueType;值的类。
	XmlElementText	xml元素的text
	
    [Demo](https://github.com/247321453/KXml/blob/master/xml2object/src/test/java/com/uutils/xml2object/Tests.java)