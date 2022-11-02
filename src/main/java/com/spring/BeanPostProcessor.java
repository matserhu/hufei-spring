package com.spring;

public interface BeanPostProcessor {


    //初始化前
    default Object postProcessBeforeInitialization(Object bean,String beanName){
        return bean;
    }

    //初始化后
    default Object postProcessAfterInitialization(Object bean,String beanName){

        System.out.println(beanName);
        return bean;
    }

}
