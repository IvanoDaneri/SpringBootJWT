package spring.ganimede.dao;

import spring.ganimede.controller.CompanyDto;
import spring.ganimede.entity.Company;

import java.util.List;

public interface CompanyService
{
    List<CompanyDto> findAll();

    CompanyDto findById(Long id) throws CompanyNotFoundException;

    List<CompanyDto> findByName(String name);

    CompanyDto findByCode(String code) throws CompanyNotFoundException;

    CompanyDto addCompany(CompanyDto companyDto) throws CompanyNotValidException;

    CompanyDto convertToDto(Company company);

    Company convertToEntity(CompanyDto companyDto);
}
