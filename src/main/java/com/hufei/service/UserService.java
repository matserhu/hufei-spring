package com.hufei.service;


import com.spring.*;

@Component("userService")
@Scope("prototype")
public class UserService implements UserInterface,BeanNameAware{   //implements InitializingBean


    @Autowired
    private OrderService orderService;

    @HufeiValue("xxx")
    private String test;

    private String beanName;

    @Override
    public void test() {
        System.out.println(test);
    }

    @Override
    public void setBeanName(String name) {
        this.beanName=name;
    }


//    @Override
//    public void afterPropertiesSet() {
//        System.out.println("初始化");
//    }
}