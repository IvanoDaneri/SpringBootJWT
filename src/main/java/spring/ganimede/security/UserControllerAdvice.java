package spring.ganimede.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import spring.ganimede.security.dao.InvalidPasswordException;
import spring.ganimede.security.dao.InvalidUserException;

@ControllerAdvice
public class UserControllerAdvice
{
    @ResponseBody
    @ExceptionHandler(InvalidUserException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String invalidUserHandler(InvalidUserException exception)
    {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String invalidPasswordHandler(InvalidPasswordException exception)
    {
        return exception.getMessage();
    }
}
