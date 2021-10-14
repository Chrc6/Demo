package com.chrc.annotalib;

/**
 * @author : chrc
 * date   : 10/12/21  11:27 AM
 * desc   :
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API类注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//@Documented
//@Inherited
public @interface ApiClass {
    // 接口类描述
    String description();

    // 接口根URL
    String basePath() default "";
}
