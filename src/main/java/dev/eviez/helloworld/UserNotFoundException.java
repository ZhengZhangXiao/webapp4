package dev.eviez.helloworld;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message) {
        super(message);
    }

    // You can add more constructors if needed
    // For example, to include the cause of the exception
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}