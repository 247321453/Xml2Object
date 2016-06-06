package com.uutils.xml2object;

import net.kk.xml.XmlReader;
import net.kk.xml.XmlWriter;
import net.kk.xml.internal.XmlObject;
import net.kk.xml.internal.XmlTypeAdapter;

import java.lang.reflect.AnnotatedElement;

public class PeopleTypeAdapter implements XmlTypeAdapter<PeopleType> {
    @Override
    public XmlObject write(XmlWriter writer, String name, AnnotatedElement element, PeopleType object) throws Exception {
        XmlObject object1=new XmlObject(name);
        object1.setType(element);
        object1.setText(""+object.ordinal());
        return object1;
    }

    @Override
    public PeopleType read(XmlReader reader, XmlObject parent, XmlObject xmlObject, Object init) throws Exception {
        int i =Integer.parseInt(xmlObject.getText());
        PeopleType[] peopleTypes=PeopleType.values();
        for(PeopleType peopleType:peopleTypes){
            if(peopleType.ordinal()==i){
                return peopleType;
            }
        }
        return null;
    }
}
