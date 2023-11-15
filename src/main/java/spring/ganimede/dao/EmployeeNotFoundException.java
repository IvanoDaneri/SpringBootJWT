package spring.ganimede.dao;

public class EmployeeNotFoundException extends RuntimeException
{
    public EmployeeNotFoundException() {
    }

    public EmployeeNotFoundException(Long id)
    {
        super("Could not find employee: " + id);
    }
    public EmployeeNotFoundException(String fiscalCode)
    {
        super("Could not find employee with fiscal code: " + fiscalCode);
    }

}
