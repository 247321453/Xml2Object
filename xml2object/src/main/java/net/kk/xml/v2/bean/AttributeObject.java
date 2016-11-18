package net.kk.xml.v2.bean;

import java.lang.reflect.Array;

public class AttributeObject {
    public AttributeObject(String namespace, String name){
        this(namespace, name, null);
    }
    public AttributeObject(String namespace, String name, String value) {
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

    public void setValue(String value) {
        this.value = value;
    }

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
        if (o instanceof AttributeObject) {
            AttributeObject other = (AttributeObject) o;
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
        return "AttributeObject{" + "name='" + name + '\'' + ", namespace='" + namespace + '\'' + ", value='"
                + value + '\'' + '}';
    }
}
