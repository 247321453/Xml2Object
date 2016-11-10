package net.kk.xml;

import net.kk.xml.annotations.XmlIgnore;

public class IXmlElement {
    /** 同级元素的位置 */
    @XmlIgnore
    protected int index;

    public int getIndex() {
        return index;
    }
}
