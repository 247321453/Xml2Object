package net.kk.xml;

import net.kk.xml.internal.Reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class XmlObject {
	public class XmlAttributeObject {
		public XmlAttributeObject(String namespace, String name, String value) {
			this.name = name;
			if (namespace != null && namespace.trim().length() == 0) {
				namespace = null;
			}
			this.namespace = namespace;
			this.value = value;
		}

		private String name;
		private String namespace;
		private String value;

		public String getName() {
			return name;
		}

		public String getNamespace() {
			return namespace;
		}

		public String getValue() {
			return value;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof XmlAttributeObject) {
				XmlAttributeObject other = (XmlAttributeObject) o;
				if (other.getName() != null && !other.getName().equals(name)) {
					return false;
				} else if (name != null) {
					return false;
				}
				if (other.getNamespace() != null && !other.getNamespace().equals(name)) {
					return false;
				} else if (namespace != null) {
					return false;
				}
				return true;
			}
			return super.equals(o);
		}

		@Override
		public String toString() {
			return "XmlAttributeObject{" + "name='" + name + '\'' + ", namespace='" + namespace + '\'' + ", value='"
					+ value + '\'' + '}';
		}
	}

	public XmlObject() {
		super();
	}

	public XmlObject(String name) {
		this();
		this.name = name;
	}

	// xml的文本
	private String text;

	// 标签名
	private String name;

	private Field tField;

	// 类型
	private Class<?> tClass;

	// 子元素，用来打印
	private boolean subItem;

	// 属性
	private List<XmlAttributeObject> attributes;
	// 子标签
	private List<XmlObject> mXmlObjects;

	private String namespace;

    /***
     * xml属性集合
     */
	public List<XmlAttributeObject> getAttributes() {
		return attributes;
	}

    /***
     * 命名空间
     * @return
     */
	public String getNamespace() {
		return namespace;
	}

    /**
     * list，map的子标签
     */
	public boolean isSubItem() {
		return subItem;
	}
    /**
     * list，map的子标签
     */
	public void setSubItem(boolean subItem) {
		this.subItem = subItem;
	}

    /***
     * 添加属性
     * @param namespace 命名空间
     * @param name 名字
     * @param value 值
     */
	public void addAttribute(String namespace, String name, String value) {
		if (name == null)
			return;
		if (attributes == null) {
			attributes = new ArrayList<XmlAttributeObject>();
		}
		XmlAttributeObject xmlAttributeObject = new XmlAttributeObject(namespace, name, value);
		int index = attributes.indexOf(xmlAttributeObject);
		if (index >= 0) {
			attributes.remove(index);
		}
		attributes.add(xmlAttributeObject);
	}

    /**
     * 空
     */
	public boolean isNULL() {
		return tClass == null;
	}

    /***
     * 添加字标签
     * @param xmlObject
     */
	public void addChild(XmlObject xmlObject) {
		if (mXmlObjects == null) {
			mXmlObjects = new ArrayList<XmlObject>();
		}
		if (xmlObject != null) {
			mXmlObjects.add(xmlObject);
		}
	}

    /***
     *  xml标签数量
     * @return
     */
    public int getChildCount() {
        return mXmlObjects == null ? 0 : mXmlObjects.size();
    }

    /***
     * 获取字标签
     * @param i
     * @return
     */
	public XmlObject getChildAt(int i) {
		if (mXmlObjects == null)
			return null;
		if (i >= 0 && i < getChildCount()) {
			return mXmlObjects.get(i);
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setText(String value) {
		this.text = value;
	}

	public void addAllChilds(Collection<XmlObject> collection) {
		if (collection != null) {
			for (XmlObject e : collection) {
				addChild(e);
			}
		}
	}

	public XmlObject getChild(String name) {
		if (name == null || mXmlObjects == null)
			return null;
		for (XmlObject t : mXmlObjects) {
			if (name.equals(t.getName())) {
				return t;
			}
		}
		return null;
	}

	public ArrayList<XmlObject> getSameChild(String name) {
		ArrayList<XmlObject> xmlObjects = new ArrayList<XmlObject>();
		if (name == null || this.mXmlObjects == null) {
			return xmlObjects;
		}
		for (XmlObject t : this.mXmlObjects) {
			if (name.equals(t.getName())) {
				xmlObjects.add(t);
			}
		}
		return xmlObjects;
	}

	public Class<?> getTClass() {
		if (tClass == null) {
			if (tField == null) {
				return null;
			}
			return tField.getType();
		}
		return tClass;
	}

	public AnnotatedElement getType() {
		return this.tField == null ? this.tClass : this.tField;
	}

	public void setType(AnnotatedElement pType) {
		if (pType instanceof Field) {
			this.tField = (Field) pType;
			this.tClass = this.tField.getType();
		} else {
			this.tField = null;
			this.tClass = (Class<?>) pType;
		}
	}

	public String toString(int start) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(name + ":");
		stringBuffer.append("" + (tClass == null ? "" : tClass.getName()));
		try {
			if (Reflect.isNormal(tClass)) {
				stringBuffer.append(" = " + text + "\n");
				return stringBuffer.toString();
			}
		} catch (Exception e) {

		}
		stringBuffer.append("{text='" + (text == null ? "" : text));
		stringBuffer.append("', attributes=" + attributes);
		stringBuffer.append(", tags=");
		if (mXmlObjects == null || mXmlObjects.size() == 0) {
			stringBuffer.append("null}\n");
		} else {
			stringBuffer.append("[\n");
			for (XmlObject xmlObject : mXmlObjects) {
				for (int i = 0; i < start + 1; i++) {
					stringBuffer.append("\t");
				}
				stringBuffer.append(xmlObject.toString(start + 1));
			}
			for (int i = 0; i < start; i++) {
				stringBuffer.append("\t");
			}
			stringBuffer.append("]\n");
		}
		return stringBuffer.toString();
	}

	@Override
	public String toString() {
		return toString(0);
	}
}
