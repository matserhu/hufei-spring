package com.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)//
@Target(ElementType.TYPE)//表示作用在类上面
public @interface ComponentScan {

    String value() default "";
}
