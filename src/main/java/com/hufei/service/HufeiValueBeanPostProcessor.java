package com.hufei.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;
import com.spring.HufeiValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class HufeiValueBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(HufeiValue.class)) {
                field.setAccessible(true);
                try {
                    field.set(bean,field.getAnnotation(HufeiValue.class).value());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        //bean
        return bean;
    }
}
