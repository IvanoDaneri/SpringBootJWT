package spring.ganimede.entity;

public enum EmployeeRole
{
    WORKER,
    EMPLOYEE,
    MANAGER;

    public static EmployeeRole fromValue(final String value)
    {
        return valueOf(value);
    }

    public String value()
    {
        return name();
    }
}
