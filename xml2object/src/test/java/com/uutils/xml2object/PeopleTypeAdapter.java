package com.uutils.xml2object;

import net.kk.xml.internal.XmlStringAdapter;

public class PeopleTypeAdapter implements XmlStringAdapter<PeopleType> {
    @Override
    public String toString(Class<PeopleType> cls,PeopleType peopleType) {
        return "" + peopleType.ordinal();
    }

    @Override
    public PeopleType toObject(Class<?> cls, String value) throws Exception {
        int i = Integer.parseInt(value);
        PeopleType[] peopleTypes = PeopleType.values();
        for (PeopleType peopleType : peopleTypes) {
            if (peopleType.ordinal() == i) {
                return peopleType;
            }
        }
        return null;
    }
}
