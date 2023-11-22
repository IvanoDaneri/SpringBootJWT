package spring.ganimede.security.dao;

public interface UserService
{
    String getCommaSeparatedAuthorityList(String userName, String password) throws InvalidUserException, InvalidPasswordException;
}
