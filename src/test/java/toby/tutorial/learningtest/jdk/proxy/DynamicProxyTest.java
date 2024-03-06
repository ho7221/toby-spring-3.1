package toby.tutorial.learningtest.jdk.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import toby.tutorial.learningtest.Hello;
import toby.tutorial.learningtest.HelloImpl;
import toby.tutorial.learningtest.UppercaseHandler;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicProxyTest {
    @Test
    public void simpleProxy(){
        Hello proxiedHello = (Hello)Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class},
                new UppercaseHandler(new HelloImpl())
        );

        assertEquals(proxiedHello.sayHello("Toby"),"HELLO TOBY");
        assertEquals(proxiedHello.sayHi("Toby"),"HI TOBY");
        assertEquals(proxiedHello.sayThankYou("Toby"),"THANK YOU TOBY");
    }

    @Test
    public void proxyFactoryBean(){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloImpl());
        pfBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertEquals(proxiedHello.sayHello("Toby"),"HELLO TOBY");
        assertEquals(proxiedHello.sayHi("Toby"),"HI TOBY");
        assertEquals(proxiedHello.sayThankYou("Toby"),"THANK YOU TOBY");
    }

    @Test
    public void pointcutAdvisor(){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloImpl());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello)pfBean.getObject();

        assertEquals(proxiedHello.sayHello("Toby"),"HELLO TOBY");
        assertEquals(proxiedHello.sayHi("Toby"),"HI TOBY");
        assertEquals(proxiedHello.sayThankYou("Toby"),"Thank you Toby");
    }

    @Test
    public void classNamePointcutAdvisor(){
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut(){
            public ClassFilter getClassFilter(){
                return new ClassFilter(){
                    public boolean matches(Class<?> clazz){
                        return clazz.getSimpleName().startsWith("HelloI");
                    }
                };
            }
        };
        classMethodPointcut.setMappedName("sayH*");
        checkAdviced(new HelloImpl(), classMethodPointcut, true);
    }

    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello) pfBean.getObject();

        if(adviced){
            assertEquals(proxiedHello.sayHello("Toby"),"HELLO TOBY");
            assertEquals(proxiedHello.sayHi("Toby"),"HI TOBY");
            assertEquals(proxiedHello.sayThankYou("Toby"),"Thank you Toby");
        }else{
            assertEquals(proxiedHello.sayHello("Toby"),"Hello Toby");
            assertEquals(proxiedHello.sayHi("Toby"),"Hi Toby");
            assertEquals(proxiedHello.sayThankYou("Toby"),"Thank You Toby");
        }
    }
    public class UppercaseAdvice implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable{
            String ret = (String)invocation.proceed();
            return ret.toUpperCase();
        }
    }

}
