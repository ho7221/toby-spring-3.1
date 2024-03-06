package toby.tutorial.user.exception;

public class TestUserServiceException extends RuntimeException {
    public TestUserServiceException(){
        super("Exception occurred with intention.");
    }
}
