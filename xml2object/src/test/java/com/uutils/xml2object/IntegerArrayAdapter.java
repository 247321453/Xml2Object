package com.uutils.xml2object;

import net.kk.xml.internal.XmlStringAdapter;

public class IntegerArrayAdapter implements XmlStringAdapter<int[]> {
    @Override
    public int[] toObject(Class<?> tClass, String val) throws Exception {
        if (val != null) {
            String[] vs = val.split(",");
            int[] is = new int[vs.length];
            for (int i = 0; i < vs.length; i++) {
                is[i] = Integer.parseInt(vs[i]);
            }
            return is;
        }
        return new int[0];
    }

    @Override
    public String toString(Class<int[]> aClass, int[] var) {
        if (var != null) {
            StringBuffer stringBuffer = new StringBuffer();
            for (Integer i : var) {
                stringBuffer.append(i);
                stringBuffer.append(",");
            }
            if (stringBuffer.length() > 0) {
                return stringBuffer.substring(0, stringBuffer.length() - 1);
            }
        }
        return "";
    }
}
