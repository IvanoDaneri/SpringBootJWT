package spring.ganimede.dao;

import spring.ganimede.controller.EmployeeDto;
import spring.ganimede.entity.Company;
import spring.ganimede.entity.Employee;
import spring.ganimede.entity.EmployeeRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeTest
{
    @Autowired
    @InjectMocks
    EmployeeServiceImpl employeeService;

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    CompanyRepository companyRepository;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void compareEmployees()
    {
        String companyCode = "0003456";

        List<EmployeeDto> employees = new ArrayList<>();
        employees.add(getEmployeeDto("Mario", "Rossi", companyCode));
        employees.add(getEmployeeDto("Giuseppe", "Verdi", companyCode));
        employees.add(getEmployeeDto("Roberto", "Abbo", companyCode));

        boolean found = false;
        for (EmployeeDto employee: employees)
        {
            if(employee.equals(getEmployeeDto("Giuseppe", "Verdi", companyCode)))
            {
                System.out.println("Found an employee equals to employee: " + employee.toString());
                found = true;
                break;
            }
        }
        
        Assert.assertTrue("Employee not found!", found);
    }

    @Test
    public void convertToEntity()
    {
        String companyCode = "0003456";

        Company company = new Company();
        company.setCode(companyCode);
        List<Company> companies = new ArrayList<>();
        companies.add(company);
        when(companyRepository.findByCode(companyCode)).thenReturn(companies);

        EmployeeDto employeeDto = getEmployeeDto("Mario", "Rossi", companyCode);
        Assert.assertNotNull(employeeService.convertToEntity(employeeDto));
    }

    private EmployeeDto getEmployeeDto(String name, String surname, String companyCode)
    {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setName(name);
        employeeDto.setSurname(surname);
        employeeDto.setRole(EmployeeRole.EMPLOYEE);
        employeeDto.setCompanyCode(companyCode);
        return employeeDto;
    }

    private Employee getEmployee(String name, String surname, String companyCode)
    {
        Employee employee = new Employee();
        employee.setName(name);
        employee.setSurname(surname);
        employee.setRole(EmployeeRole.EMPLOYEE);
        Company company = new Company();
        company.setCode(companyCode);
        employee.setCompany(company);
        return employee;
    }
}
