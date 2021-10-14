package com.netease.qa.annotationlib;

import com.squareup.javapoet.ClassName;

/**
 * @author : chrc
 * date   : 10/13/21  10:43 AM
 * desc   :
 */

public class ImplAnnotationInfo {
    public String name = "";
    public ClassName implClass;

    public ImplAnnotationInfo(String name, ClassName implClass) {
        this.name = name;
        this.implClass = implClass;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImplAnnotationInfo)) {
            return false;
        }
        return name.equals(((ImplAnnotationInfo) obj).name);
    }
}