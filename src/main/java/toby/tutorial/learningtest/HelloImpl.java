package toby.tutorial.learningtest;

public class HelloImpl implements Hello{

    public String sayHello(String name) {
        return "Hello " + name;
    }

    public String sayHi(String name) {
        return "Hi " + name;
    }

    public String sayThankYou(String name) {
        return "Thank you " + name;
    }
}
