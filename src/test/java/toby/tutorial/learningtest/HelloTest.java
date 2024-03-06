package toby.tutorial.learningtest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.lang.reflect.Proxy;

public class HelloTest {

    Hello proxiedHello = (Hello)Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class[]{Hello.class},
            new UppercaseHandler(new HelloImpl())
    );

    @Test
    public void simpleProxy(){
        assertEquals(proxiedHello.sayHello("Toby"),"HELLO TOBY");
    }
}
