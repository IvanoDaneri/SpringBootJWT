package spring.ganimede.security.dao;

public class InvalidPasswordException extends RuntimeException
{
    public InvalidPasswordException(String password) {
        super("Invalid password: " + password);
    }
}
