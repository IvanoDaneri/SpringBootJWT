package spring.ganimede.controller;

import spring.ganimede.dao.CompanyNotFoundException;
import spring.ganimede.dao.CompanyNotValidException;
import spring.ganimede.dao.EmployeeNotFoundException;
import spring.ganimede.dao.EmployeeNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerNotFoundAdvice
{
    @ResponseBody
    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String employeeNotFoundHandler(EmployeeNotFoundException exception)
    {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(CompanyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String companyNotFoundHandler(CompanyNotFoundException exception)
    {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(EmployeeNotValidException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    String employeeNotValidHandler(EmployeeNotValidException exception)
    {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(CompanyNotValidException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    String companyNotValidHandler(CompanyNotValidException exception)
    {
        return exception.getMessage();
    }
}
