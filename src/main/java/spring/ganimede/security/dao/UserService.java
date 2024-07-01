package spring.ganimede.security.dao;

import java.util.Map;

public interface UserService
{
    String AUTHORITIES = "authorities";

    String getCommaSeparatedAuthorityList(String userName, String password) throws InvalidUserException, InvalidPasswordException;

    Map<String, Object> getPermissions(String userName, String password) throws InvalidUserException, InvalidPasswordException;
}
