package spring.ganimede.dao;

import spring.ganimede.controller.CompanyDto;

public class CompanyNotValidException extends RuntimeException
{
    public CompanyNotValidException()
    {
        super("Company is not valid!");
    }

    public CompanyNotValidException(CompanyDto companyDto)
    {
        super("Company: " + companyDto.toString() + " is not valid!");
    }

    public CompanyNotValidException(CompanyDto companyDto, String cause)
    {
        super("Sql error in insert/update company: " + companyDto.toString() + ", cause: " + cause);
    }
}
