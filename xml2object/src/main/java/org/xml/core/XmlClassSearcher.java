package org.xml.core;

import java.util.List;

public interface XmlClassSearcher {
    /**
     * 根据属性和元素的name，返回派生类的类型
     * @param tags 属性和元素的name
     * @return 返回派生类的类型
     */
     Class<?> getSubClass(List<String> tags);
}
