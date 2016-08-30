package net.kk.xml.internal;

public class XmlStringAdapters {

    public XmlStringAdapter<Object> ObjectStringAdapter = new XmlStringAdapter<Object>() {
        @Override
        public Object toObject(Class<?> objectClass, String val) throws Exception {
            Object object=null;
            try {
                object = Reflect.wrapperValue(objectClass, val);
            }catch (Exception e){

            }
            return object;
        }

        @Override
        public String toString(Class<Object> objectClass, Object var) {
            return var == null ? "" : var.toString();
        }
    };

}
