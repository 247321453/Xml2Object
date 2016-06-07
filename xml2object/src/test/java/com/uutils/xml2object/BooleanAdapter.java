package com.uutils.xml2object;

import net.kk.xml.internal.XmlStringAdapter;

public class BooleanAdapter implements XmlStringAdapter<Boolean> {
    @Override
    public Boolean toObject(Class<?> tClass, String val) throws Exception {
        return "true".equalsIgnoreCase(val) || "1".equalsIgnoreCase(val);
    }

    @Override
    public String toString(Class<Boolean> booleanClass, Boolean var) {
        if (var == null) {
            var = Boolean.FALSE;
        }
        return var.toString();
    }
}
