package com.spring;
import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HufeiApplicationContext {

    private Class configClass;

    private Map<String,BeanDefinition> beanDefinitionMap=new HashMap<>();

    private Map<String,Object> singletonObjects=new HashMap<>();//单例池->就用来存单例对象的

    private List<BeanPostProcessor> beanPostProcessorList=new ArrayList<>();

    public HufeiApplicationContext(Class configClass) {
        this.configClass = configClass;

        //扫描
        scan(configClass);

        //
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();

            if (beanDefinition.getScope().equals("singleton")) {


                //创建单例Bean
                Object bean = createBean(beanName, beanDefinition);
                //放入单例池
                singletonObjects.put(beanName,bean);
            }
        }
    }

    //真正的创建单例Bean(依赖注入)
    private Object createBean(String beanName,BeanDefinition beanDefinition){
        Class clazz = beanDefinition.getType();

        Object instance=null;
        try {

            instance = clazz.getConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)){
                    field.setAccessible(true);
                    field.set(instance,getBean(field.getName()));
                }
            }

            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            //初始化前
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance=beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
            }

            //判断对象是不是实现了这个接口
            //初始化
            if (instance instanceof InitializingBean){
                ((InitializingBean) instance).afterPropertiesSet();

            }

            //初始化后
            //BeanPostProcessor-------AOP就是这么实现的(底层基于BeanPostProcessor)
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance=beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            }


        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return instance;
    }




    public Object getBean(String beanName){
        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new NullPointerException();
        }
        BeanDefinition beanDefinition=beanDefinitionMap.get(beanName);
        if (beanDefinition.getScope().equals("singleton")) {

            Object singletonBean = singletonObjects.get(beanName);

            if (singletonBean==null){
                singletonBean=createBean(beanName,beanDefinition);
                singletonObjects.put(beanName,singletonBean);
            }
            return singletonBean;
        }else {
            //原型
            Object prototypeBean = createBean(beanName, beanDefinition);
            return prototypeBean;
        }
    }



    private void scan(Class configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);

            //扫描路径-->com.hufei.service
            String path= componentScanAnnotation.value();// com/hufei/service
            //
            path=path.replace(".","/");

            //System.out.println(path);

            ClassLoader classLoader = HufeiApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File file=new File(resource.getFile());

            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    String absolutePath = f.getAbsolutePath();

                    //System.out.println(absolutePath);

                    absolutePath=absolutePath.substring(absolutePath.indexOf("com"),absolutePath.indexOf(".class"));
                    absolutePath=absolutePath.replace("\\",".");

                    //System.out.println(absolutePath);

                    //你想看这个类（路径）有没有注解，还得把它变成类
                    try {
                        Class<?> aClass = classLoader.loadClass(absolutePath);
                        if (aClass.isAnnotationPresent(Component.class)) {

                            //判断这个类是不是实现了某个接口
                            //为什么不用 instanceof ? 原因：instanceof是用在某个对象上，而这里是class
                            if (BeanPostProcessor.class.isAssignableFrom(aClass)) {
                                BeanPostProcessor instance = (BeanPostProcessor) aClass.getConstructor().newInstance();
                                beanPostProcessorList.add(instance);
                            }

                            //取名字
                            Component componentAnnotation = aClass.getAnnotation(Component.class);
                            String beanName = componentAnnotation.value();
                            //如果没有指定Bean的名字，则用spring默认生成名字的方法
                            if ("".equals(beanName)){
                                beanName= Introspector.decapitalize(aClass.getSimpleName());
                            }

                            BeanDefinition beanDefinition=new BeanDefinition();
                            beanDefinition.setType(aClass);

                            if (aClass.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = aClass.getAnnotation(Scope.class);
                                String value = scopeAnnotation.value();
                                //.....看到底是单例还是原型
                                beanDefinition.setScope(value);
                            }else {
                                //单例
                                beanDefinition.setScope("singleton");
                            }
                            beanDefinitionMap.put(beanName,beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
