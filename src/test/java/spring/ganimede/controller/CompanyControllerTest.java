package spring.ganimede.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import spring.ganimede.SpringBootApp;
import spring.ganimede.dao.TestUtil;
import spring.ganimede.entity.CompanyType;
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
public class CompanyControllerTest extends Logon
{
    private static String URL_REST_FIND_ALL_COMPANIES = "http://localhost:8094/springBootRest/companies";
    private static String URL_REST_FIND_COMPANY_BY_CODE = "http://localhost:8094/springBootRest/companies/findByCode";
    private static String URL_REST_ADD_COMPANY = "http://localhost:8094/springBootRest/companies/addCompany";

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
                ResponseEntity<String> response = restTemplate.exchange(URL_REST_FIND_ALL_COMPANIES, HttpMethod.GET, request, String.class);
                List<CompanyDto> companyDtoList = convertResult(response.getBody());
                Assert.notNull(companyDtoList);
                Assert.notEmpty(companyDtoList);

                // Find a company by id
                String url = URL_REST_FIND_ALL_COMPANIES + "/" + Long.toString(companyDtoList.get(0).getId());
                ResponseEntity<CompanyDto> responseFromId = restTemplate.exchange(url, HttpMethod.GET, request, CompanyDto.class);
                Assert.notNull(responseFromId.getBody());
            }
            else
            {
                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.getForObject(URL_REST_FIND_ALL_COMPANIES, String.class);
                List<CompanyDto> companyDtoList = convertResult(result);
                Assert.notNull(companyDtoList);
                Assert.notEmpty(companyDtoList);

                // Find a company by id
                String url = URL_REST_FIND_ALL_COMPANIES + "/" + Long.toString(companyDtoList.get(0).getId());
                CompanyDto companyDto = restTemplate.getForObject(url, CompanyDto.class);
                Assert.notNull(companyDto);
            }
        }
        catch (RestClientException e)
        {
            String msg = String.format("Error in calling rest for url: [%s], cause: [%s]", URL_REST_FIND_ALL_COMPANIES, e.getMessage());
            fail(msg);
        }
        catch (Exception e)
        {
            String msg = String.format("Error in creating rest client for url: [%s], cause: [%s]", URL_REST_FIND_ALL_COMPANIES, e.getMessage());
            fail(msg);
        }
    }

    @Test
    public void test2_findByCode()
    {
        try
        {
            String companyCode = "0003465";
            CompanyDto result = findByCode(companyCode);
            Assert.notNull(result);
        }
        catch (AssertionError error)
        {
            fail(error.getMessage());
        }
    }

    @Test
    public void test3_addCompany()
    {
        try
        {
            // Insert new company
            String companyCode = "0123456";
            CompanyDto companyDto = TestUtil.getCompany("Cybertech s.p.a.", companyCode, "Via Nazionale 48, Milano", CompanyType.HIGH_TECH);
            CompanyDto result = addCompany(companyDto);
            Assert.notNull(result);

            // Update company
            companyDto.setAddress("Via Nazionale 50, Milano");
            result = addCompany(companyDto);
            Assert.notNull(result);

            result = findByCode(companyCode);
            Assert.notNull(result);
        }
        catch (AssertionError error)
        {
            fail(error.getMessage());
        }
    }

    private CompanyDto addCompany(final CompanyDto companyDto) throws AssertionError
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

            HttpEntity<CompanyDto> request = new HttpEntity<>(companyDto, headers);

            // Get rest template
            RestTemplate restTemplate = new RestTemplate();
            // Rest call postForObject
            CompanyDto result = restTemplate.postForObject(URL_REST_ADD_COMPANY, request, CompanyDto.class);
            return result;
        }
        catch (RestClientException e)
        {
            String msg = String.format("Error in calling rest for url: [%s], cause: [%s]", URL_REST_ADD_COMPANY, e.getMessage());
            throw new AssertionError(msg);
        }
        catch (Exception e)
        {
            String msg = String.format("Error in creating rest client for url: [%s], cause: [%s]", URL_REST_ADD_COMPANY, e.getMessage());
            throw new AssertionError(msg);
        }
    }


    private CompanyDto findByCode(String companyCode) throws AssertionError
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


    // This method allows to un-marshall a json structure with a root list
    // (json structure must be mapped to a java List<>)
    private List<CompanyDto> convertResult(String result)
    {
        Type listType = new TypeToken<ArrayList<CompanyDto>>()
        {}.getType();

        List<CompanyDto> companyDtoList = gson.fromJson(result, listType);
        return companyDtoList;
    }

}
