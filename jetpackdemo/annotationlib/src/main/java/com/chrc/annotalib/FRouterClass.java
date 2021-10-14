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
public @interface FRouterClass {
    // 接口类描述
    String action_id();
    String action_destination();
    String fragment_id();
    String fragment_name() default "";
    String fragment_arguments_name();
    String fragment_arguments_argType();
    String fragment_arguments_defaultValue();

    String methodCollection() default  "action_id,action_destination,fragment_id,fragment_name,fragment_arguments_name,fragment_arguments_argType,fragment_arguments_defaultValue";

}
