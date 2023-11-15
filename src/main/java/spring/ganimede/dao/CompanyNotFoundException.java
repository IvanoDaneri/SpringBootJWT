package spring.ganimede.dao;

public class CompanyNotFoundException extends RuntimeException
{
    public CompanyNotFoundException() {
    }

    public CompanyNotFoundException(Long id)
    {
        super("Could not find company with id: " + id);
    }
    public CompanyNotFoundException(String code)
    {
        super("Could not find company with company code: " + code);
    }
}
