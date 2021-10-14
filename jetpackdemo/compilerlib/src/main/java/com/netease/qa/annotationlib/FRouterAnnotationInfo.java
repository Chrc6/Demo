package com.netease.qa.annotationlib;

import com.squareup.javapoet.ClassName;

/**
 * @author : chrc
 * date   : 10/13/21  10:43 AM
 * desc   :
 */

public class FRouterAnnotationInfo {
    public String className;
    public String classValue;

    public FRouterAnnotationInfo(String name, String classValue) {
        this.className = name;
        this.classValue = classValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FRouterAnnotationInfo)) {
            return false;
        }
        return className.equals(((FRouterAnnotationInfo) obj).className);
    }
}