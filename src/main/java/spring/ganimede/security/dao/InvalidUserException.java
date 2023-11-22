package spring.ganimede.security.dao;

public class InvalidUserException extends RuntimeException
{
    public InvalidUserException(String user) {
        super("Invalid user: " + user);
    }
}
