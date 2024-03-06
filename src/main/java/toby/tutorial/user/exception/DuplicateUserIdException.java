package toby.tutorial.user.exception;

public class DuplicateUserIdException extends RuntimeException{
    public DuplicateUserIdException(String message, Throwable cause){
        super(message, cause);
    }
}
