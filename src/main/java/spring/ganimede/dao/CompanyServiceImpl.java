package spring.ganimede.dao;

import spring.ganimede.controller.CompanyDto;
import spring.ganimede.entity.Company;
import spring.ganimede.logger.AppLogger;
import spring.ganimede.logger.AppLoggerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService
{
    private AppLogger logger = AppLoggerService.getLogger(CompanyServiceImpl.class.getName());

    @Autowired
    CompanyRepository companyRepository;

    @Override
    public List<CompanyDto> findAll()
    {
        List<CompanyDto> companyDtos = new ArrayList<>();
        List<Company> companies = companyRepository.findAll();
        companies.forEach(company -> companyDtos.add(convertToDto(company)));
        return companyDtos;
    }

    @Override
    public CompanyDto findById(Long id) throws CompanyNotFoundException
    {
        return convertToDto(companyRepository.findById(id).orElseThrow(() -> new CompanyNotFoundException(id)));
    }

    @Override
    public List<CompanyDto> findByName(String name) {
        List<CompanyDto> companyDtos = new ArrayList<>();
        List<Company> companies = companyRepository.findByName(name);
        companies.forEach(company -> companyDtos.add(convertToDto(company)));
        return companyDtos;
    }

    @Override
    public CompanyDto findByCode(String code) throws CompanyNotFoundException
    {
        List<Company> companies = companyRepository.findByCode(code);
        if(companies == null || companies.size() == 0)
        {
            throw new CompanyNotFoundException(code);
        }

        return convertToDto(companies.get(0));
    }

    @Override
    public CompanyDto addCompany(CompanyDto companyDto) throws CompanyNotValidException
    {
        if(!CompanyDto.isValid(companyDto))
            throw new CompanyNotValidException(companyDto);

        List<Company> companies = companyRepository.findByCode(companyDto.getCode());
        if(companies == null || companies.size() == 0)
        {
            logger.info("Add company: " + companyDto.toString());
            return convertToDto(companyRepository.save(convertToEntity(companyDto)));
        }
        else
        {
            logger.info("Update company: " + companies.get(0).toString());
            return convertToDto(companyRepository.save(updateEntity(companies.get(0), companyDto)));
        }
    }

    private Company updateEntity(Company company, CompanyDto companyDto)
    {
        company.setType(companyDto.getType());
        company.setAddress(companyDto.getAddress());
        return company;
    }

    @Override
    public CompanyDto convertToDto(Company company)
    {
        CompanyDto companyDto = new CompanyDto();
        BeanUtils.copyProperties(company, companyDto);
        return companyDto;
    }

    @Override
    public Company convertToEntity(CompanyDto companyDto)
    {
        Company company = new Company();
        BeanUtils.copyProperties(companyDto, company);
        return company;
    }
}
