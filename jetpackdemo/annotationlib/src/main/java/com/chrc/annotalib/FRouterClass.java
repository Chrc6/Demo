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
    String startDestination() default "";
    String action_id() default "";
    String action_destination() default "";
    String fragment_id() default "";
    String fragment_name() default "";
    String fragment_arguments_name() default "";
    String fragment_arguments_argType() default "";
    String fragment_arguments_defaultValue() default "";

    String methodCollection() default  "action_id,action_destination,fragment_id,fragment_name,fragment_arguments_name,fragment_arguments_argType,fragment_arguments_defaultValue";

}
