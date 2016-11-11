package net.kk.xml;

import android.os.Bundle;

import net.kk.xml.internal.Reflect;
import net.kk.xml.internal.XmlConstructorAdapter;
import net.kk.xml.internal.XmlStringAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlOptions {
    private boolean debug = false;
    private boolean useSetMethod = true;
    //xml缩进
    private boolean useSpace = false;
    private boolean ignoreStatic = true;
    private Map<Class<?>, XmlStringAdapter<?>> mXmlTypeAdapterMap;
    private Map<Class<?>, XmlConstructorAdapter> xmlConstructorAdapterMap;
    private List<Class<?>> mIgnoreClasses;
    /** 忽略tag的大小写*/
    private boolean ignoreTagCase=true;
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

    public boolean isIgnore(Class<?> cls) {
        if (mIgnoreClasses != null) {
            return mIgnoreClasses.contains(cls);
        }
        return false;
    }

    public Map<Class<?>, XmlStringAdapter<?>> getXmlTypeAdapterMap() {
        return mXmlTypeAdapterMap;
    }

    public Map<Class<?>, XmlConstructorAdapter> getXmlConstructorAdapterMap() {
        return xmlConstructorAdapterMap;
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

    public boolean isIgnoreTagCase() {
        return ignoreTagCase;
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

    public boolean isIgnoreStatic() {
        return ignoreStatic;
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

        /***
         * 忽略这些类，在write的时候忽略
         *
         * @param cls
         */
        public Builder ignore(Class<?> cls) {
            if (mOptions.mIgnoreClasses == null) {
                mOptions.mIgnoreClasses = new ArrayList<Class<?>>();
            }
            mOptions.mIgnoreClasses.add(cls);
            return this;
        }

        public XmlOptions build() {
            return mOptions;
        }

        public Builder dontIgnoreStatic() {
            mOptions.ignoreStatic = false;
            return this;
        }

        public Builder dontIgnoreTagCase() {
            mOptions.ignoreTagCase = false;
            return this;
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

        public Builder registerConstructorAdapter(Class<?> cls, XmlConstructorAdapter xmlTypeAdapter) {
            if (mOptions.xmlConstructorAdapterMap == null) {
                mOptions.xmlConstructorAdapterMap = new HashMap<Class<?>, XmlConstructorAdapter>();
            }
            mOptions.xmlConstructorAdapterMap.put(Reflect.wrapper(cls), xmlTypeAdapter);
            return this;
        }

        public Builder registerTypeAdapter(Class<?> cls, XmlStringAdapter<?> xmlTypeAdapter) {
            if (mOptions.mXmlTypeAdapterMap == null) {
                mOptions.mXmlTypeAdapterMap = new HashMap<Class<?>, XmlStringAdapter<?>>();
            }
            mOptions.mXmlTypeAdapterMap.put(Reflect.wrapper(cls), xmlTypeAdapter);
            return this;
        }
    }
}
