package net.kk.xml;

import net.kk.xml.annotations.XmlElementMap;
import net.kk.xml.internal.Reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class XmlObjectReader {
	XmlReader reader;
	XmlOptions options;

	public XmlObjectReader(XmlReader reader) {
		this.reader = reader;
		this.options = reader.getOptions();
	}

	public Object read(XmlObject parent, XmlObject xmlObject, Object init) throws Exception {
		if (xmlObject == null) {
			return null;
		} else if (xmlObject.getType() == null) {
			if (reader.DEBUG)
				System.out.println(xmlObject.getName() + "'s type is null ");
			return null;
		}
		Class<?> pClass = xmlObject.getTClass();
		String name = xmlObject.getName();
		if (reader.isNormal(xmlObject.getType())) {
			if (reader.DEBUG)
				System.out.println("create normal " + xmlObject.getName() + " " + pClass);
			Object obj = null;
			try {
				obj = reader.getAdapter(pClass).toObject(pClass, xmlObject.getText());
			} catch (Throwable e) {
			}
			return obj;
		} else if (pClass.isArray()) {
			List<XmlObject> xmlObjects = getItems(parent, xmlObject.getType(), name);
			return array(xmlObjects, pClass, init);
		} else if (Collection.class.isAssignableFrom(pClass)) {
			List<XmlObject> xmlObjects = getItems(parent, xmlObject.getType(), name);
			return list(xmlObjects, pClass, init, reader.getListClass(xmlObject.getType()));
		} else if (Map.class.isAssignableFrom(pClass)) {
			List<XmlObject> xmlObjects = getItems(parent, xmlObject.getType(), name);
			return map(xmlObjects, pClass, init, reader.getMapClass(xmlObject.getType()));
		} else {
			// attribute
			// text
			// sub tag
			if (reader.DEBUG)
				System.out.println("create other " + xmlObject.getName() + " " + pClass);
			return object(xmlObject, pClass, init);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T object(XmlObject xmlObject, Class<T> pClass, Object parent) throws Exception {
		T t = (parent == null) ? Reflect.create(pClass) : (T) parent;
		// attr
		List<XmlObject.XmlAttributeObject> xmlAttributeObjects = xmlObject.getAttributes();
		if (xmlAttributeObjects != null) {
			if (reader.DEBUG) {
				System.out.println(xmlObject.getName() + " attr = " + xmlObject.getAttributes().size());
			}
			for (XmlObject.XmlAttributeObject e : xmlAttributeObjects) {
				setAttribute(t, e.getNamespace(), e.getName(), e.getValue());
			}
		}
		setText(t, xmlObject.getText());
		int count = xmlObject.size();
		List<String> oldtags = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			XmlObject el = xmlObject.get(i);
			String name = el.getName();
			if (oldtags.contains(name))
				continue;
			Field field = reader.getTagFiled(pClass, name);
			if (field == null) {
				if (reader.DEBUG)
					System.out.println("no find field " + name);
				continue;
			}
			el.setType(field);
			oldtags.add(name);
			Object val = Reflect.get(field, t);
			Object obj = read(xmlObject, el, val);
			if (obj != null && !Modifier.isFinal(field.getModifiers())) {
				Reflect.set(field, t, obj, reader.getOptions().isUseSetMethod());
			}
		}
		return t;
	}

	private void setAttribute(Object object, String namespace, String tag, String value) throws Exception {
		if (object == null || tag == null)
			return;
		if (reader.DEBUG)
			System.out.println("np=" + namespace + ",name=" + tag + ", value=" + value);
		Collection<Field> fields = Reflect.getFileds(object.getClass());
		for (Field field : fields) {
			String np = reader.getNamespace(field);
			String name = reader.getAttributeName(field);
			if (tag.equals(name)
					&& ((namespace == null && np == null) || (namespace != null && namespace.equals(np)))) {
				Object val = reader.getAdapter(field.getType()).toObject(field.getType(), value);// Reflect.wrapper(field.getType(),
																									// value);
				if (val != null) {
					Reflect.set(field, object, val, reader.getOptions().isUseSetMethod());
				}
				if (reader.DEBUG)
					System.out.println(tag + " set " + value);
				break;
			}
		}
	}

	private void setText(Object object, String value) throws Exception {
		if (object == null)
			return;
		Collection<Field> fields = Reflect.getFileds(object.getClass());
		for (Field field : fields) {
			if (reader.isXmlElementText(field)) {
				Class<?> cls = field.getType();
				Object val = reader.getAdapter(cls).toObject(cls, value);
				if (val != null) {
					Reflect.set(field, object, val, reader.getOptions().isUseSetMethod());
				}
				break;
			}
		}
	}

	private List<XmlObject> getItems(XmlObject xmlObject, AnnotatedElement field, String name) {
		List<XmlObject> xmlObjects;
		if (reader.getOptions().isSameAsList()) {
			// System.out.println(name+" -> "+xmlObject);
			xmlObjects = xmlObject.getElementList(name);
		} else {
			String subtag = reader.getItemTagName(field);
			XmlObject subObject = xmlObject.getElement(name);
			if (subObject != null) {
				xmlObjects = subObject.getElementList(subtag);
			} else {
				xmlObjects = null;
			}
		}
		return xmlObjects;
	}

	@SuppressWarnings("unchecked")
	private <T> T array(List<XmlObject> xmlObjects, Class<T> pClass, Object object) throws Exception {
		if (xmlObjects == null) {
			return null;
		}
		Class<?> sc = pClass.getComponentType();
		int count = xmlObjects.size();
		T t;
		if (object != null) {
			t = (T) object;
		} else {
			t = (T) Array.newInstance(sc, count);
		}
		for (int i = 0; i < count; i++) {
			XmlObject xmlObject = xmlObjects.get(i);
			xmlObject.setType(sc);
			Object o = read(null, xmlObject, null);
			if (o != null) {
				Array.set(t, i, o);
			}
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	private <T> T map(List<XmlObject> xmlObjects, Class<T> pClass, Object object, Class<?>[] subClass)
			throws Exception {
		if (xmlObjects == null || subClass == null || subClass.length < 2) {
			return null;
		}
		T t;
		if (object == null) {
			t = Reflect.create(pClass, subClass);
			if (reader.DEBUG)
				System.out.println("create map " + pClass.getName());
		} else {
			t = (T) object;
		}
		if (t == null)
			return t;
		if (reader.DEBUG)
			System.out.println(" put " + subClass[0] + "," + subClass[1] + " size=" + xmlObjects.size());
		for (XmlObject xmlObject : xmlObjects) {
			Class<?> kc = subClass[0];
			XmlObject tk = xmlObject.get(XmlElementMap.KEY);
			tk.setType(kc);
			Object k = read(xmlObject, tk, null);
			Class<?> vc = subClass[1];
			XmlObject tv = xmlObject.get(XmlElementMap.VALUE);
			tv.setType(vc);
			Object v = read(xmlObject, tv, null);
			if (reader.DEBUG) {
				System.out.println(xmlObject.getName() + " put " + (tk != null) + "=" + (tv != null));
				System.out.println(xmlObject.getName() + " put " + k + "=" + v);
			}
			if (k != null)
				Reflect.call(t.getClass(), t, "put", k, v);
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	private <T> T list(List<XmlObject> xmlObjects, Class<T> pClass, Object object, Class<?> subClass) throws Exception {
		if (xmlObjects == null) {
			return null;
		}
		if (reader.DEBUG)
			System.out.println("list " + subClass.getName());
		T t;
		if (object == null) {
			t = Reflect.create(pClass, subClass);
			if (reader.DEBUG)
				System.out.println("create list " + pClass.getName());
		} else {
			t = (T) object;
		}
		if (t != null) {
			// 多种派生类
			// boolean d = XmlClassSearcher.class.isAssignableFrom(subClass);
			for (XmlObject xmlObject : xmlObjects) {
				Class<?> sc = subClass;
				xmlObject.setType(sc);
				Object sub = read(null, xmlObject, null);
				if (sub != null)
					Reflect.call(t.getClass(), t, "add", sub);
				else {
					if (reader.DEBUG)
						System.out.println(xmlObject.getName() + "@" + sc.getName() + " is null");
				}
			}
		}
		return t;
	}
}
