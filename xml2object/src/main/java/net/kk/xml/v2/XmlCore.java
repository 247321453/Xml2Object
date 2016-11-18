package net.kk.xml.v2;

import net.kk.xml.v2.adapter.XmlConstructorAdapter;
import net.kk.xml.v2.adapter.XmlTextAdapter;
import net.kk.xml.v2.annotations.XmlAttribute;
import net.kk.xml.v2.annotations.XmlInnerText;
import net.kk.xml.v2.annotations.XmlTag;
import net.kk.xml.v2.bean.IXmlElement;
import net.kk.xml.v2.bean.TagObject;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class XmlCore {
    public static final String DEF_ENCODING = "UTF-8";
    protected XmlOptions mOptions;

    public XmlCore(XmlOptions options) {
        mOptions = options;
    }

    public Reflect on(Object obj) {
        return Reflect.on(obj.getClass(), mOptions);
    }

    public Reflect on(Class<?> pClass) {
        return Reflect.on(pClass, mOptions);
    }
    public TagObject make(Object obj) throws Exception {
        if(IXmlElement.class.isInstance(obj)){
            int pos = on(obj).get(obj, "pos");
            return make(obj.getClass(), pos);
        }
        return make(obj.getClass(), 0);
    }
    public TagObject make(Class<?> pClass,int pos){
        XmlTag tag = pClass.getAnnotation(XmlTag.class);
        if (tag != null) {
            return new TagObject(tag.value(),tag.namespace(), 0, pos);
        }
        return new TagObject("unknown", null, 0, 0);
    }

    public String getClassTag(Class<?> cls) {
        if (cls == null) return null;
        XmlTag tag = cls.getAnnotation(XmlTag.class);
        if (tag != null) {
            return tag.value();
        }
        return cls.getSimpleName();
    }

    protected boolean isEmtry(String text) {
        return text == null || text.length() == 0;
    }

    protected boolean isXmlText(AnnotatedElement ae){
        return ae.getAnnotation(XmlInnerText.class)!=null;
    }

    protected boolean matchTag(Field field, String name) {
        if (isEmtry(name)) return false;
        XmlTag xmlTag = field.getAnnotation(XmlTag.class);
        if (xmlTag == null) {
            if (mOptions.isIgnoreTagCase()) {
                return field.getName().equalsIgnoreCase(name);
            } else {
                return field.getName().equals(name);
            }
        } else {
            String defname = xmlTag.value();
            if (mOptions.isIgnoreTagCase()) {
                if (name.equalsIgnoreCase(defname)) {
                    return true;
                }
            } else {
                if (name.equals(defname)) {
                    return true;
                }
            }
            String[] alias = xmlTag.alias();
            if (alias != null) {
                for (String alia : alias) {
                    if (mOptions.isIgnoreTagCase()) {
                        if (name.equalsIgnoreCase(alia)) {
                            return true;
                        }
                    } else {
                        if (name.equals(alia)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    protected boolean matchAttribute(Field field, String name, String namespace) {
        if (isEmtry(name)) return false;
        XmlAttribute attribute = null;
        if (!isEmtry(namespace)) {
            attribute = field.getAnnotation(XmlAttribute.class);
            if (attribute == null) {
                return false;
            }
            if (!namespace.equals(attribute.namespace())) {
                return false;
            }
        }
        if (attribute == null) {
            attribute = field.getAnnotation(XmlAttribute.class);
        }
        if (attribute == null) {
            if (mOptions.isIgnoreTagCase()) {
                return field.getName().equalsIgnoreCase(name);
            } else {
                return field.getName().equals(name);
            }
        } else {
            String defname = attribute.value();
            if (mOptions.isIgnoreTagCase()) {
                if (name.equalsIgnoreCase(defname)) {
                    return true;
                }
            } else {
                if (name.equals(defname)) {
                    return true;
                }
            }
            String[] alias = attribute.alias();
            if (alias != null) {
                for (String alia : alias) {
                    if (mOptions.isIgnoreTagCase()) {
                        if (name.equalsIgnoreCase(alia)) {
                            return true;
                        }
                    } else {
                        if (name.equals(alia)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    protected XmlTextAdapter getTypeAdapter(Class<?> pClass) {
        return mOptions.getXmlTypeAdapterMap().get(ReflectUtils.wrapper(pClass));
    }

    protected XmlConstructorAdapter getConstructor(Class<?> pClass) {
        return mOptions.getXmlConstructorAdapterMap().get(ReflectUtils.wrapper(pClass));
    }

    public <T> T create(Class<T> pClass, Object parent) {
        T o = null;
        XmlConstructorAdapter constructorAdapter = getConstructor(pClass);
        if (constructorAdapter != null) {
            try {
                o = constructorAdapter.create(pClass, parent);
            } catch (Exception e) {

            }
            return o;
        }
        try {
            if (pClass.isMemberClass() && (pClass.getModifiers() & Modifier.STATIC) == 0) {
                //内部类
                o = Reflect.on(pClass, mOptions).create(new Class[]{parent.getClass()}, new Object[]{parent});
            } else {
                o = Reflect.on(pClass, mOptions).create(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }
}
