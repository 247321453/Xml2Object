package org.xml.convert;

import java.lang.reflect.AnnotatedElement;

public abstract class NumberAdapter<T extends Number> extends TypeAdapter<T> {
    @Override
    public void write(TypeToken parent, AnnotatedElement type, String name, Number number) {
        if (name == null || name.length() == 0) {
            name = getTagName(type);
        }
        TypeToken typeToken = new TypeToken(name);
        typeToken.setType(type);
        if (number != null) {
            typeToken.setText(number.toString());
            parent.add(typeToken);
        }
    }
}