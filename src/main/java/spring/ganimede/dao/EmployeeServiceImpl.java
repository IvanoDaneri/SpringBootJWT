package spring.ganimede.dao;

import spring.ganimede.controller.EmployeeDto;
import spring.ganimede.entity.Company;
import spring.ganimede.entity.Employee;
import spring.ganimede.entity.EmployeeRole;
import spring.ganimede.logger.AppLoggerService;
import spring.ganimede.logger.AppLogger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService
{
    private AppLogger logger = AppLoggerService.getLogger(EmployeeServiceImpl.class.getName());

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Override
    public List<EmployeeDto> findAll() {
        List<EmployeeDto> employeesDto = new ArrayList<>();
        List<Employee> employees = employeeRepository.findAll();
        employees.forEach(employee -> employeesDto.add(convertToDto(employee)));
        return employeesDto;
    }

    @Override
    public EmployeeDto findById(Long id) throws EmployeeNotFoundException {
        return convertToDto(employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id)));
    }

    @Override
    public List<EmployeeDto> findByName(String name) {
        List<EmployeeDto> employeesDto = new ArrayList<>();
        List<Employee> employees = employeeRepository.findByName(name);
        employees.forEach(employee -> employeesDto.add(convertToDto(employee)));
        return employeesDto;
    }

    @Override
    public List<EmployeeDto> findBySurname(String surname) {
        List<EmployeeDto> employeesDto = new ArrayList<>();
        List<Employee> employees = employeeRepository.findBySurname(surname);
        employees.forEach(employee -> employeesDto.add(convertToDto(employee)));
        return employeesDto;
    }

    @Override
    public List<EmployeeDto> findByNameSurname(String name, String surname)
    {
        List<EmployeeDto> employeesDto = new ArrayList<>();
        try
        {
            Stream<Employee> employees = employeeRepository.findByNameAndSurnameReturnStream(name, surname);
            employees.forEach(employee -> employeesDto.add(convertToDto(employee)));
        }
        catch (NoSuchElementException e)
        {
        }

        return employeesDto;
    }

    @Override
    public EmployeeDto findByFiscalCode(String fiscalCode) throws EmployeeNotFoundException {
        List<Employee> employees = employeeRepository.findByFiscalCode(fiscalCode);
        if(employees == null || employees.size() == 0)
        {
            throw new EmployeeNotFoundException(fiscalCode);
        }

        return convertToDto(employees.get(0));
    }

    @Override
    public List<EmployeeDto> findByRole(EmployeeRole role) {
        List<EmployeeDto> employeesDto = new ArrayList<>();
        List<Employee> employees = employeeRepository.findByRole(role);
        employees.forEach(employee -> employeesDto.add(convertToDto(employee)));
        return employeesDto;
    }

    @Override
    public EmployeeDto addEmployee(EmployeeDto employeeDto) throws EmployeeNotValidException, CompanyNotFoundException
    {
        if (!EmployeeDto.isValid(employeeDto))
            throw new EmployeeNotValidException(employeeDto);

        List<Employee> employees = employeeRepository.findByFiscalCode(employeeDto.getFiscalCode());
        if(employees == null || employees.size() == 0)
        {
            logger.info("Add employee: " + employeeDto.toString());
            return convertToDto(employeeRepository.save(convertToEntity(employeeDto)));
        }
        else
        {
            logger.info("Update employee: " + employees.get(0).toString());
            return convertToDto(employeeRepository.save(updateEntity(employees.get(0), employeeDto)));
        }
    }

    @Override
    public EmployeeDto convertToDto(Employee employee)
    {
        EmployeeDto employeeDto = new EmployeeDto();
        BeanUtils.copyProperties(employee, employeeDto);
        if(employee.getCompany() != null)
        {
            employeeDto.setCompanyName(employee.getCompany().getName());
            employeeDto.setCompanyCode(employee.getCompany().getCode());
        }

        return employeeDto;
    }

    @Override
    public Employee convertToEntity(EmployeeDto employeeDto) throws CompanyNotFoundException
    {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDto, employee);
        List<Company> companies = companyRepository.findByCode(employeeDto.getCompanyCode());
        if(companies == null || companies.size() == 0)
        {
            throw new CompanyNotFoundException(employeeDto.getCompanyCode());
        }

        employee.setCompany(companies.stream().findFirst().get());
        return employee;
    }

    private Employee updateEntity(Employee employee, EmployeeDto employeeDto) throws CompanyNotFoundException
    {
        List<Company> companies = companyRepository.findByCode(employeeDto.getCompanyCode());
        if(companies == null || companies.size() == 0)
        {
            throw new CompanyNotFoundException(employeeDto.getCompanyCode());
        }

        employee.setCompany(companies.stream().findFirst().get());
        employee.setBirthday(employeeDto.getBirthday());
        employee.setRole(employeeDto.getRole());
        return employee;
    }

}
