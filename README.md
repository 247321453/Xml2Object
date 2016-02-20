# KXml
介绍
=============
	xml与对象的互转，支持自定义的类，注解后，随便混淆
	数据类型：List，数组，Map，基本数据类型，enum。
	注：没有注解的字段不会处理
	Android Studio用法
	compile 'com.github.247321453:xml2object:1.3.8'
	注解：
	XmlAttribute	xml属性注解（基本数据类型，enum）
	XmlElement	xml元素注解（自定义类型，数组，基本数据类型，enum）
	XmlElementList  xml的list元素注解
	XmlElementMap	xml的map元素注解，keyType：key的类，valueType;值的类。
	XmlElementText	xml元素的text
	
	对象转xml
	new XmlWriter().toXml(Object object, OutputStream outputStream, String encoding);
	xml转对象
	T t = new XmlReader().from(InputStream inputStream, Class<T> pClass, String encoding);
