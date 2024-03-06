package toby.tutorial.learningtest;

public class HelloUppercase implements Hello{
    Hello hello;
    public HelloUppercase(Hello target){
        this.hello = target;
    }

    public String sayHi(String name) {
        return hello.sayHi(name).toUpperCase();
    }

    public String sayHello(String name) {
        return hello.sayHello(name).toUpperCase();
    }

    public String sayThankYou(String name) {
        return hello.sayThankYou(name).toUpperCase();
    }
}
