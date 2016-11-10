package net.kk.xml;

import net.kk.xml.annotations.XmlIgnore;

import java.util.Comparator;

public class IXmlElement {
    /** 同级元素的位置 */
    @XmlIgnore
    protected int index;

    public int getIndex() {
        return index;
    }
    public static final Comparator<Object> ASC= new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            if(o1 instanceof IXmlElement && o2 instanceof IXmlElement){
                IXmlElement e1 = (IXmlElement)o1;
                IXmlElement e2 = (IXmlElement)o2;
                return e1.getIndex() - e2.getIndex();
            }
            return 0;
        }
    };
}
