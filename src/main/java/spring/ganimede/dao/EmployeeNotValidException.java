package spring.ganimede.dao;

import spring.ganimede.controller.EmployeeDto;

public class EmployeeNotValidException extends RuntimeException
{
    public EmployeeNotValidException()
    {
        super("Employee is not valid!");
    }

    public EmployeeNotValidException(EmployeeDto employeeDto)
    {
        super("Employee: " + employeeDto.toString() + " is not valid!");
    }

    public EmployeeNotValidException(EmployeeDto employeeDto, String cause)
    {
        super("Sql error in insert/update employee: " + employeeDto.toString() + ", cause: " + cause);
    }
}
