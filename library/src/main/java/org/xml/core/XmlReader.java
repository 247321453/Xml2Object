package org.xml.core;

/***
 * {@link org.xml.bean.Tag } 转对象
 */
public class XmlReader {
    static final String DEF_ENCODING = "UTF-8";


//
//    private Tag createSubTag(Class<?> cls, String name)
//            throws
//            NoSuchFieldException, IllegalAccessException,
//            InstantiationException, InvocationTargetException {
//        Tag tag = new Tag(name);
//        if (cls == null || name == null) return null;
//        Field[] fields = Reflect.getFileds(cls);
//        Field tfield = null;
//        for (Field field : fields) {
//            if (!isXmlTag(field))
//                continue;
//            XmlTag xmltag = field.getAnnotation(XmlTag.class);
//            if (xmltag != null) {
//                if (name.equals(xmltag.value())) {
//                    tfield = field;
//                    break;
//                }
//            }
//        }
//        if (tfield == null) {
//            tfield = Reflect.getFiled(cls, name);
//        }
//        if (tfield != null) {
//            Log.d("xml", "create find " + name);
//            setTagbyField(tfield, tag);
//            return tag;
//        }
//        Log.d("xml", "create no find " + tag);
//        return tag;
//    }
//
//    private void setTagbyField(Field tfield, Tag tag) {
//        if (tfield != null) {
//            tag.setClass(tfield.getType());
//            if (tfield.getType().isArray()) {
//                tag.setIsArray(true);
//            } else if (isXmlList(tfield)) {
//                XmlList xmlList = tfield.getAnnotation(XmlList.class);
//                tag.setIsArray(true);
//                tag.setSubClasss(xmlList.value());
//            } else if (isXmlMap(tfield)) {
//                XmlMap xmlMap = tfield.getAnnotation(XmlMap.class);
//                tag.setIsMap(true);
//                tag.setSubClasss(xmlMap.value());
//            } else {
//
//            }
//        }
//    }
}
