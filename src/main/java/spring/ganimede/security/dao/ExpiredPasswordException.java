package spring.ganimede.security.dao;

public class ExpiredPasswordException extends RuntimeException
{
    public ExpiredPasswordException(String username) {
        super("User: " + username + " - Password expired");
    }
}
