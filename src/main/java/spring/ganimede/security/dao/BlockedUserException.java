package spring.ganimede.security.dao;

public class BlockedUserException extends RuntimeException
{
    public BlockedUserException(String user) {
        super("Blocked user: " + user);
    }
}
