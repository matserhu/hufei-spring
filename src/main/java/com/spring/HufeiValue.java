package com.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)//
@Target(ElementType.FIELD )//表示作用在字段上面
public @interface HufeiValue {

    String value() default "";
}
