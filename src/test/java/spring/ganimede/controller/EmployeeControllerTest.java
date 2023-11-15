package spring.ganimede.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import spring.ganimede.SpringBootApp;
import spring.ganimede.dao.TestUtil;
import spring.ganimede.entity.EmployeeRole;
import io.jsonwebtoken.lang.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {SpringBootApp.class})
@TestPropertySource(locations = "classpath:test.application.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeControllerTest extends Logon
{
    private static String URL_REST_FIND_COMPANY_BY_CODE = "http://localhost:8092/springBootRest/companies/findByCode";
    private static String URL_REST_FIND_ALL_EMPLOYEES = "http://localhost:8092/springBootRest/employees";
    private static String URL_REST_FIND_EMPLOYEE_BY_FISCAL_CODE = "http://localhost:8092/springBootRest/employees/findByFiscalCode";
    private static String URL_REST_ADD_EMPLOYEE = "http://localhost:8092/springBootRest/employees/addEmployee";

    private Gson gson = new Gson();


    @Test
    public void test1_findAll()
    {
        try
        {
            if(jwtSecurity)
            {
                RestTemplate restTemplate = new RestTemplate();
                // Set JWT token in Authorization property of request header
                HttpEntity<Void> request = getRequest();
                ResponseEntity<String> response = restTemplate.exchange(URL_REST_FIND_ALL_EMPLOYEES, HttpMethod.GET, request, String.class);
                List<EmployeeDto> employeeDtoList = convertResult(response.getBody());
                Assert.notNull(employeeDtoList);
                Assert.notEmpty(employeeDtoList);

                // Find a company by id
                String url = URL_REST_FIND_ALL_EMPLOYEES + "/" + Long.toString(employeeDtoList.get(0).getId());
                ResponseEntity<EmployeeDto> responseFromId = restTemplate.exchange(url, HttpMethod.GET, request, EmployeeDto.class);
                Assert.notNull(responseFromId.getBody());

            }
            else
            {
                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.getForObject(URL_REST_FIND_ALL_EMPLOYEES, String.class);
                List<EmployeeDto> employeeDtoList = convertResult(result);
                Assert.notNull(employeeDtoList);
                Assert.notEmpty(employeeDtoList);

                // Find a company by id
                String url = URL_REST_FIND_ALL_EMPLOYEES + "/" + Long.toString(employeeDtoList.get(0).getId());
                EmployeeDto employeeDto = restTemplate.getForObject(url, EmployeeDto.class);
                Assert.notNull(employeeDto);
            }
        }
        catch (RestClientException e)
        {
            String msg = String.format("Error in calling rest for url: [%s], cause: [%s]", URL_REST_FIND_ALL_EMPLOYEES, e.getMessage());
            fail(msg);
        }
        catch (Exception e)
        {
            String msg = String.format("Error in creating rest client for url: [%s], cause: [%s]", URL_REST_FIND_ALL_EMPLOYEES, e.getMessage());
            fail(msg);
        }
    }


    @Test
    public void test2_findByCode()
    {
        try
        {
            String fiscalCode = "ROSMAR72E23E488K";
            EmployeeDto result = findByCode(fiscalCode);
            Assert.notNull(result);
        }
        catch (AssertionError error)
        {
            fail(error.getMessage());
        }
    }

    private EmployeeDto findByCode(String fiscalCode) throws AssertionError
    {
        String url = URL_REST_FIND_EMPLOYEE_BY_FISCAL_CODE + "/" + fiscalCode;

        try
        {
            if(jwtSecurity)
            {
                RestTemplate restTemplate = new RestTemplate();
                // Set JWT token in Authorization property of request header
                HttpEntity<Void> request = getRequest();
                ResponseEntity<EmployeeDto> responseFromId = restTemplate.exchange(url, HttpMethod.GET, request, EmployeeDto.class);
                return responseFromId.getBody();
            }
            else
            {
                RestTemplate restTemplate = new RestTemplate();
                EmployeeDto result = restTemplate.getForObject(url, EmployeeDto.class);
                return result;
            }
        }
        catch (RestClientException e)
        {
            String msg = String.format("Error in calling rest for url: [%s], cause: [%s]", url, e.getMessage());
            throw new AssertionError(msg);
        }
        catch (Exception e)
        {
            String msg = String.format("Error in creating rest client for url: [%s], cause: [%s]", url, e.getMessage());
            throw new AssertionError(msg);
        }
    }

    @Test
    public void test3_addEmployee()
    {
        try
        {
            // Get company
            String companyCode = "0003465";
            CompanyDto companyDto = findCompanyByCode(companyCode);
            Assert.notNull(companyDto);

            // Insert employee
            String fiscalCode = "DNRVNA72E12E488J";
            EmployeeDto employeeDto = TestUtil.getEmployee("Vania", "Dondero", TestUtil.getDate("12-Feb-1972"),
                    fiscalCode, EmployeeRole.WORKER, companyDto.getCode());
            EmployeeDto employeeResult = addEmployee(employeeDto);
            Assert.notNull(employeeResult);

            // Update employee
            employeeDto.setBirthday(TestUtil.getDate("16-Feb-1974"));
            employeeResult = addEmployee(employeeDto);
            Assert.notNull(employeeResult);

            employeeResult = findByCode(fiscalCode);
            Assert.notNull(employeeResult);
        }
        catch (RestClientException e)
        {
            String msg = String.format("Error in calling rest for url: [%s], cause: [%s]", URL_REST_ADD_EMPLOYEE, e.getMessage());
            fail(msg);
        }
        catch (Exception e)
        {
            String msg = String.format("Error in creating rest client for url: [%s], cause: [%s]", URL_REST_ADD_EMPLOYEE, e.getMessage());
            fail(msg);
        }
    }

    private CompanyDto findCompanyByCode(String companyCode) throws AssertionError
    {
        String url = URL_REST_FIND_COMPANY_BY_CODE + "/" + companyCode;

        try
        {
            if(jwtSecurity)
            {
                RestTemplate restTemplate = new RestTemplate();
                // Set JWT token in Authorization property of request header
                HttpEntity<Void> request = getRequest();
                ResponseEntity<CompanyDto> responseFromId = restTemplate.exchange(url, HttpMethod.GET, request, CompanyDto.class);
                return responseFromId.getBody();
            }
            else
            {
                RestTemplate restTemplate = new RestTemplate();
                CompanyDto result = restTemplate.getForObject(url, CompanyDto.class);
                return result;
            }
        }
        catch (RestClientException e)
        {
            String msg = String.format("Error in calling rest for url: [%s], cause: [%s]", url, e.getMessage());
            throw new AssertionError(msg);
        }
        catch (Exception e)
        {
            String msg = String.format("Error in creating rest client for url: [%s], cause: [%s]", url, e.getMessage());
            throw new AssertionError(msg);
        }
    }

    private EmployeeDto addEmployee(final EmployeeDto employeeDto) throws AssertionError
    {
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Set JWT token in Authorization property of request header
            if(jwtSecurity)
            {
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add(AUTHORIZATION_PROPERTY, token);
            }

            HttpEntity<EmployeeDto> request = new HttpEntity<>(employeeDto, headers);

            // Get rest template
            RestTemplate restTemplate = new RestTemplate();
            // Rest call postForObject
            EmployeeDto employeeDtoResult = restTemplate.postForObject(URL_REST_ADD_EMPLOYEE, request, EmployeeDto.class);
            return employeeDtoResult;
        }
        catch (RestClientException e)
        {
            String msg = String.format("Error in calling rest for url: [%s], cause: [%s]", URL_REST_ADD_EMPLOYEE, e.getMessage());
            throw new AssertionError(msg);
        }
        catch (Exception e)
        {
            String msg = String.format("Error in creating rest client for url: [%s], cause: [%s]", URL_REST_ADD_EMPLOYEE, e.getMessage());
            throw new AssertionError(msg);
        }
    }

    // This method allows to un-marshall a json structure with a root list
    // (json structure must be mapped to a java List<>)
    private List<EmployeeDto> convertResult(String result)
    {
        Type listType = new TypeToken<ArrayList<EmployeeDto>>()
        {}.getType();

        List<EmployeeDto> employeeDtoList = gson.fromJson(result, listType);
        return employeeDtoList;
    }

    // This method allows to un-marshall a json structure with a root list
    // (json structure must be mapped to a java List<>)
    private List<CompanyDto> convertResultCompany(String result)
    {
        Type listType = new TypeToken<ArrayList<CompanyDto>>()
        {}.getType();

        List<CompanyDto> companyDtoList = gson.fromJson(result, listType);
        return companyDtoList;
    }
}
