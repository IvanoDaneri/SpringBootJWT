package spring.ganimede.controller;

import spring.ganimede.security.CredentialsDto;
import io.jsonwebtoken.lang.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.fail;

public class Logon
{
    protected static String URL_REST_LOGON = "http://localhost:8092/springBootRest/logon";

    // Authorization property of request header
    protected static String AUTHORIZATION_PROPERTY = "Authorization";

    @Value("${spring.security.jwt}")
    protected Boolean jwtSecurity;
    @Value("${spring.security.usr}")
    protected String user;
    @Value("${spring.security.passwd}")
    protected String password;
    protected static String token = null;

    @Before
    public void startup()
    {
        // JWT security not configured
        if(!jwtSecurity)
            return;

        // Send credentials to logon into the system and obtain JWT token
        try
        {
            if (token == null)
            {
                token = logon(user, password);
                Assert.notNull(token);
            }
        }
        catch (AssertionError error)
        {
            fail(error.getMessage());
        }
    }

    protected String logon(String user, String password) throws AssertionError
    {
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Build request
            HttpEntity<CredentialsDto> request = new HttpEntity<>(new CredentialsDto(user, password), headers);
            // Get rest template
            RestTemplate restTemplate = new RestTemplate();
            // Rest call postForObject
            String token = restTemplate.postForObject(URL_REST_LOGON, request, String.class);
            return token;
        }
        catch (RestClientException e)
        {
            String msg = String.format("Error in calling rest for url: [%s]", URL_REST_LOGON);
            throw new AssertionError(msg);
        }
        catch (Exception e)
        {
            String msg = String.format("Error in creating rest client for url: [%s]", URL_REST_LOGON);
            throw new AssertionError(msg);
        }
    }

    protected HttpEntity<Void> getRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(AUTHORIZATION_PROPERTY, token);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return request;
    }

}
