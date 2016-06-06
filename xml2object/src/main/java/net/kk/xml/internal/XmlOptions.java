package net.kk.xml.internal;

import net.kk.xml.internal.bind.Reflect;

import java.util.HashMap;
import java.util.Map;

public class XmlOptions {
    private boolean debug = false;
    private boolean useSetMethod = true;
    private boolean useSpace = false;
    private Map<String, XmlTypeAdapter<?>> mXmlTypeAdapterMap;
    /***
     * true
     * <pre>
     *     list
     *     list
     *
     *     list
     *     list
     * </pre>
     * <pre>
     * lists
     *     list
     *     list
     * lists
     * </pre>
     */
    private boolean sameAsList = false;

    private boolean useNoAnnotation = true;

    public Map<String, XmlTypeAdapter<?>> getXmlTypeAdapterMap() {
        return mXmlTypeAdapterMap;
    }

    /***
     * @return use setXXXX
     */
    public boolean isUseSetMethod() {
        return useSetMethod;
    }

    public boolean isDebug() {
        return debug;
    }

    /***
     * @return
     */
    public boolean isUseSpace() {
        return useSpace;
    }

    public boolean isSameAsList() {
        return sameAsList;
    }

    /***
     * @return use no annotation
     */
    public boolean isUseNoAnnotation() {
        return useNoAnnotation;
    }

    private XmlOptions() {

    }

    public final static XmlOptions DEFAULT = new XmlOptions.Builder().build();

    public static class Builder {
        private XmlOptions mOptions;

        public Builder() {
            mOptions = new XmlOptions();
        }

        public Builder(XmlOptions options) {
            this();
            if (options != null) {
                mOptions.useSetMethod = options.useSetMethod;
            }
        }

        public XmlOptions build() {
            return mOptions;
        }

        public Builder dontUseSetMethod() {
            mOptions.useSetMethod = false;
            return this;
        }

        public Builder useSpace() {
            mOptions.useSpace = true;
            return this;
        }

        public Builder ignoreNoAnnotation() {
            mOptions.useNoAnnotation = false;
            return this;
        }

        public Builder enableSameAsList() {
            mOptions.sameAsList = true;
            return this;
        }

        public Builder debug() {
            mOptions.debug = true;
            return this;
        }

        public Builder registerTypeAdapter(Class<?> cls,XmlTypeAdapter<?> xmlTypeAdapter){
            if(mOptions.mXmlTypeAdapterMap==null){
                mOptions.mXmlTypeAdapterMap=new HashMap<String, XmlTypeAdapter<?>>();
            }
            mOptions.mXmlTypeAdapterMap.put(Reflect.wrapper(cls).getName(), xmlTypeAdapter);
            return this;
        }
    }
}
