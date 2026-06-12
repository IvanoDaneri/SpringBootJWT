package spring.ganimede.security.dao;

public class InvalidPasswordException extends RuntimeException
{
    public InvalidPasswordException(String username) {
        super("User: " + username + " - Invalid password");
    }
}
