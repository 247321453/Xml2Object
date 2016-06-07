package net.kk.xml.internal;

public class XmlTypeAdapters {

    public XmlStringAdapter<Object> ObjectStringAdapter = new XmlStringAdapter<Object>() {
        @Override
        public Object toObject(Class<?> objectClass, String val) throws Exception {
            return Reflect.wrapperValue(objectClass, val);
        }

        @Override
        public String toString(Class<Object> objectClass, Object var) {
            return var == null ? "" : var.toString();
        }
    };

}
