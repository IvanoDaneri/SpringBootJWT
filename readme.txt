----------------------------
SPRING-BOOT-REST-JWT APP 1.0
----------------------------

----------------------------------
I) GENERAL APPLICATION DESCRIPTION
----------------------------------

Educational application in SpringBoot that exposes http rest GET and POST with two different SpringSecurity configurations that we will see later.
The application exposes two rest controllers that manage the CRUD of companies and employees entities (CompanyController, EmployeeController).
The backend app saves entities on a database, in our case OracleXE.
Db creation scripts and initial data population scripts are present for two different db users, test user and production user
(the junit tests have property files that map  test user).

In the SpringBoot application:

- the Cross-Origin Resource Sharing service has been disabled because it would have blocked POST type HTTP requests

- DTO validation on POST-type rests has been added, i.e.:
    - @NotEmpty and @NotNull annotations on the CompanyDto and EmployeeDto class fields
    - @Valid annotation on the rest method parameter

- we define ControllerAdvice that manage a series of exceptions thrown by the rest Controllers and
  which will be re-thrown to the rest client (classes: ControllerNotFoundAdvice, UserControllerAdvice)

Some rest url examples:

- curl to request all company records: curl http://localhost:8092/springBootRest/companies
- curl to request company record with id=1: curl http://localhost:8092/springBootRest/companies/1
- curl to request all employee record: curl http://localhost:8092/springBootRest/employees
- curl to request employee records with id=2: curl http://localhost:8092/springBootRest/employees/2

Regarding Junit test of rest services (get and post):

- a specific SoapUI project has been released: springBootRestClient-soapui-project.xml
    - the client pass credentials to rest Logon to get valid JWT token from SpringBoot app
    - protected rests can be called using JWT token get from Logon

- Test java classes have been written for controllers (CompanyController, EmployeeController). Test classes performs CRUD entities by means of rest calls.
  CompanyControllerTest and EmployeeControllerTest inherits from Logon class that call rest logon in startup and save JWT session token.
  CompanyControllerTest and EmployeeControllerTest set JWT session token in header of http request for rest calls.

- on controller tests the annotation used was:

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {SpringBootApp.class})

   This annotation launches SpringBootApp including rest services on the port configured in the properties file
   and therefore allows test class to invoke the rest services using the RestTemplate factory and the getForObject methods for rest GET
   and postForObject for the rest POST (really in the case of JWT configuration the exchange method is used because we have to pass the request with
   a header containing JWT session token in "Authorization" property)

Provisioning phase:

- before starting the application you must execute db script in this order:

    - create_user.sql (or create_test_user.sql for test db)
    - init_db.sql (or init_test_db.sql for test db)
    - init_security_db.sql (or init_security_test_db.sql for test db)
    - run Junit class: UserServiceTest.java to insert users in test db. Please, to
      insert users in production db, open test.application.properties and modify
      db properties as here:
      spring.datasource.username=test_spring_db -> spring.datasource.username=spring_db
      spring.datasource.password=test_spring_db -> spring.datasource.password=spring_db
      As an alternative to insert users, it's possible to use:
      - insert_users_db.sql for production db
      - insert_users_test_db.sql for test db

--------------------------------------
II) SECURITY APPLICATION CONFIGURATION
--------------------------------------

Let's see the part of the application that concerns security (which is certainly the most interesting part of app).
Two different SpringSecurity configurations have been implemented which are in alternative:

1) basic configuration: any user is authorized to access the URLs of the rest exposed by rest controllers (CompanyController, EmployeeController).
2) JWT configuration: carries out Authentication and Authorization by implementing the OAuth 2.0 JWT (Json Web Token) standard which provides
   a validity token with a fixed expiry time.

Enable of security configuration 1 or 2 is operated by the jwtSecurity property defined in the application properties (application.properties, property: spring.security.jwt).
Validity token duration is fixed by the sessionDuration property defined in the application properties (application.properties, property: spring.security.jwt.session-duration).

Let's see the WebSecurityConfig SpringBoot configuration class:


@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    private static final RequestMatcher PROTECTED_URLS = new OrRequestMatcher(new AntPathRequestMatcher("/companies/**"), new AntPathRequestMatcher("/employees/**"));

    @Value("${spring.security.jwt}")
    private Boolean jwtSecurity;

    @Override
    public void configure( HttpSecurity http ) throws Exception
    {
        // Init - This is configuration for preflight CORS approval
        List<String> allowedMethods=new ArrayList<>();
        allowedMethods.add("GET");
        allowedMethods.add("POST");
        allowedMethods.add("PUT");
        allowedMethods.add("DELETE");
        CorsConfiguration cors=new CorsConfiguration();
        cors.setAllowedMethods(allowedMethods);
        http.cors().configurationSource(request -> cors.applyPermitDefaultValues());
        // End - This is configuration for preflight CORS approval

        // JWT security enabled
        if(jwtSecurity)
        {
            http.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET,"/companies/**").hasAuthority(PermissionEnum.AUTH_COMPANY_READ.name())      // CompanyController GET must have AUTH_COMPANY_READ permission
                    .antMatchers(HttpMethod.POST,"/companies/**").hasAuthority(PermissionEnum.AUTH_COMPANY_ADD.name())      // CompanyController POST must have AUTH_COMPANY_ADD permission
                    .antMatchers(HttpMethod.GET,"/employees/**").hasAuthority(PermissionEnum.AUTH_EMPLOYEE_READ.name())     // EmployeeController GET must have AUTH_EMPLOYEE_READ permission
                    .antMatchers(HttpMethod.POST,"/employees/**").hasAuthority(PermissionEnum.AUTH_EMPLOYEE_ADD.name())     // EmployeeController POST must have AUTH_EMPLOYEE_ADD permission
                    .antMatchers(HttpMethod.POST, "/logon").permitAll()                                                     // Permit logon url to everyone to pass credentials and get JWT token
                    .requestMatchers(PROTECTED_URLS)                                                                        // These are urls protected by JWTAuthorizationFilter
                    .authenticated()
                    // This configuration disable default CORS (Cross-Origin Resource Sharing) configuration
                    // that blocks rest POST calls from a client
                    .and()
                    .csrf().disable()
                    // This configuration disable all other default configurations
                    .formLogin().disable()
                    .httpBasic().disable()
                    .logout().disable();
        }
        // No security enabled for rest urls
        else
        {
            http
                    .authorizeRequests()
                    // Add url exceptions to http security configuration to avoid http authentication for rest url path
                    .antMatchers("/companies/**", "/employees/**", "/simpleEmployees/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic()
                    // This configuration disable default CORS (Cross-Origin Resource Sharing) configuration
                    // that blocks rest POST calls from a client
                    .and()
                    .csrf().disable();
        }
    }

}

1) Authentication phase.
The configuration that implements JWT includes a logon rest url that implements the Authentication phase (class: UserController):

- verify the credentials passed in the logon POST header
- retrieves the roles assigned to the user
- retrieves permissions linked to roles
- generates and returns back a validity token (JWT session token, which contains the encrypted permissions of the user's role) with a fixed expiration duration (as I said, read from the application properties file)

The token must be passed in the header of http request for rest calls to the protected rest urls (CompanyController,  EmployeeController), creating "Authorization" property and set value of property with token.
Example of token returned by logon method:
Bearer: eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJHYWxpbGVvSldUIiwic3ViIjoibXlBZG1pbiIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJBVVRIX0NPTVBBTllfQUREIn0seyJhdXRob3JpdHkiOiJBVVRIX0VNUExPWUVFX1JFQUQifSx7ImF1dGhvcml0eSI6IkFVVEhfQ09NUEFOWV9SRUFEIn0seyJhdXRob3JpdHkiOiJBVVRIX0VNUExPWUVFX0FERCJ9XSwiaWF0IjoxNzE5MjMxNDgxLCJleHAiOjE3MTkyMzIwODF9.Eo0wAQPqhi30uIo0Kzg50eCS8wWyEuSYtJPN03xgGSD-7aMsDaK3gDmEAqSEVurid1Xq8nYkiVasjeZuwT-WwQ

2) Authorization phase.
The Authorization phase is managed by the JWTAuthorizationFilter filter inserted in the filter chain which operates in the following way:

- intercepts the URLs of the protected rests and checks the presence of the token (extracts the value of the "Authorization" property in the request header and checks valid format)
- verifies the validity of the token and extracts the Claim;
- extracts the list of permissions from the Claim and enables URL calls if the token's permissions include the permissions expected for that URL.

   For example the rest url:

        http://localhost:8092/springBootRest/companies

   provides permission:

        PermissionEnum.AUTH_COMPANY_READ

   as configured in WebSecurityConfig:

        .antMatchers(HttpMethod.GET,"/companies/**").hasAuthority(PermissionEnum.AUTH_COMPANY_READ.name())

   therefore, PermissionEnum.AUTH_COMPANY_READ must be present among the permissions encrypted in the token for that user, otherwise the filter will return: 404 Access Forbidden

As mentioned, the authentication/authorization management uses the standard protocol JSON Web Token (JWT, see https://jwt.io/).
The authentication/authorization process is described in the Softtek article:

Token-based API authentication with Spring and JWT

The protocol includes:

- an authentication server that verifies the credentials and authenticates the client by generate a validity token with a fixed duration validity (JWT session token)
- an authorization server which accepts the token for each protected request and verify whether the client has permission to access that resource.

3) In addition, WebSecurityConfig enables Cross-Origin Sharing Resource (CORS) in that way:

        // Init - This is configuration for preflight CORS approval
        List<String> allowedMethods=new ArrayList<>();
        allowedMethods.add("GET");
        allowedMethods.add("POST");
        allowedMethods.add("PUT");
        allowedMethods.add("DELETE");
        CorsConfiguration cors=new CorsConfiguration();
        cors.setAllowedMethods(allowedMethods);
        http.cors().configurationSource(request -> cors.applyPermitDefaultValues());
        // End - This is configuration for preflight CORS approval

Remember that CORS must be enabled and configured server side as we do in our SpringBootJWT application.
In our example allow http CORS request from any origin.
Here's some notes about CORS and CSRF (Cross-Site Request Forgery).

CORS (Cross-Origin Resource Sharing):
Purpose: CORS is a security mechanism that allows or restricts web browsers to make requests to a different domain (origin) than the one that served the web page.
Scenario: Suppose you have a frontend application running on https://myfrontend.com that needs to fetch data from an API hosted on https://api.example.com.
CORS ensures that the browser can safely make requests across different origins.
Implementation: CORS is implemented on the server side. The server includes specific HTTP headers (such as Access-Control-Allow-Origin) in its responses to indicate which origins are allowed to access its resources.
Security Benefit: CORS prevents unauthorized cross-origin requests, enhancing security.

CSRF (Cross-Site Request Forgery):
Purpose: CSRF is an attack where an attacker tricks a user into performing an action on a website without their knowledge or consent.
Scenario: Imagine you’re logged into your online banking application. An attacker sends you a malicious link that, when clicked, initiates a money transfer from your account to theirs.
Implementation: CSRF attacks exploit the user’s existing session (usually via cookies). The attacker crafts a request (e.g., a money transfer) and tricks the user into executing it.
Security Benefit: To defend against CSRF, servers can use techniques like token-based protection (e.g., including a CSRF token in forms) or the “cookie-to-header” pattern.

In summary:

CORS deals with cross-origin requests and controls which domains can access resources.
CSRF is an attack that exploits a user’s session to perform unintended actions on their behalf.


-------------------------
III) DOCKER CONFIGURATION
-------------------------

Application SpringBootJwt is configured to run in Docker Compose with two containers:

    - application container
    - db container

In project is present Dockerfile to create SpringBootJwt image.
Script .bat in docker folder allows to:

    - create data volume (1.create_volume_one_shot.bat)
    - crate db container (2.run_db.bat)
    - initialize db (3.init_db.bat)

Once db initialization is completed (and persisted on data volume), db container can be removed.
Final, we can launch Docker Compose with docker-compose.dev.yml and all it works!
