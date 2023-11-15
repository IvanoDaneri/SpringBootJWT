package spring.ganimede.dao;

import spring.ganimede.controller.EmployeeDto;
import spring.ganimede.entity.Employee;
import spring.ganimede.entity.EmployeeRole;

import java.util.List;

public interface EmployeeService
{
    List<EmployeeDto> findAll();

    EmployeeDto findById(Long id) throws EmployeeNotFoundException;

    List<EmployeeDto> findByName(String name);

    List<EmployeeDto> findBySurname(String surname);

    List<EmployeeDto> findByNameSurname(String name, String surname);

    EmployeeDto findByFiscalCode(String fiscalCode) throws EmployeeNotFoundException;

    List<EmployeeDto> findByRole(EmployeeRole role);

    EmployeeDto addEmployee(EmployeeDto newEmployee) throws EmployeeNotValidException;

    EmployeeDto convertToDto(Employee employee);

    Employee convertToEntity(EmployeeDto employeeDto) throws CompanyNotFoundException;
}
