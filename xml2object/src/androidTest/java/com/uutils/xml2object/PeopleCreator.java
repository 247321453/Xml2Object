package com.uutils.xml2object;

import android.util.Log;

import org.xml.core.XmlClassSearcher;

import java.util.List;

/**
 * Created by Hasee on 2016/2/2.
 */
public class PeopleCreator implements XmlClassSearcher {
    @Override
    public Class<?> getSubClass(List<String> tags) {
        Log.v("xml", "get = " + tags);
        if(tags.contains("as")){
            return Man.class;
        }
        return Woman.class;
    }
}
