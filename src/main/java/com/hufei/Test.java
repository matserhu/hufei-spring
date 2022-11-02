package com.hufei;

import com.hufei.service.UserInterface;
import com.hufei.service.UserService;
import com.spring.HufeiApplicationContext;

public class Test {

    public static void main(String[] args) {

        //模拟spring容器
        //先忽略懒加载
        //扫描-->创建单例Bean
        HufeiApplicationContext applicationContext=new HufeiApplicationContext(AppConfig.class);

//        UserService userService= (UserService) applicationContext.getBean("userService");
//        userService.test();


        UserInterface userService= (UserInterface) applicationContext.getBean("userService");
        userService.test();
    }
}
