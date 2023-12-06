SPRINGBOOT-REST-JWT APP 1.0
---------------------------

Educational application in SpringBoot that exposes http rest GET and POST with two different SpringSecurity configurations that we will see later.
The application exposes two rest controllers that manage the CRUD of companies and employees entities (CompanyController, EmployeeController).
The backend saves entities on a database (in our case OracleXE) for which the db creation and initial population scripts are present
for two different users, test user and production user (the junit tests have the properties files that map  test user).

In the SpringBoot application:

- the Cross-Origin Resource Sharing service has been disabled because it would have blocked POST type HTTP requests
- DTO validation on POST-type rests has been added, i.e.:
- @NotEmpty and @NotNull annotations on the CompanyDto and EmployeeDto dtos
- @Valid annotation on the rest method parameter
- we define ControllerAdvices that manage a series of exceptions thrown by the rest Controllers and which will be re-thrown to the rest client (classes: ControllerNotFoundAdvice, UserControllerAdvice)

Some rest url examples:

- curl to request complete company records: curl http://localhost:8092/springBootRest/companies
- curl to request company record with id=1: curl http://localhost:8092/springBootRest/companies/1
- curl to request complete employee record: curl http://localhost:8092/springBootRest/employees
- curl curl to request employee records with id=2: curl http://localhost:8092/springBootRest/employees/2

Regarding Junit test of rest services (get and post):

- a specific SoapUI project has been released: springBootRestClient-soapui-project.xml
    - a rest client pass credentials to rest Logon to get valid JWT token from SpringBoot app
    - protected rests can be called using JWT token get from previous call

- a test java class has been written for both services (only for CompanyService) and controllers (CompanyController, EmployeeController). Both test classes performr CRUDs.

- on controller tests the annotation used was:

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {SpringBootApp.class})

   This annotation launches SpringBootApp including rest services on the port configured in the properties file
   and therefore allows you to invoke the rest services from the test class using the RestTemplate factory and the getForObject methods for rest GET and postForObject for the rest POST (really in the case of JWT configuration the exchange method is used because we have to pass the request with a header containing the valorized properties)

Provisioning phase:

- before starting the application you must execute db script in this order:

    - create_user.sql (and create_test_user.sql for test environment)
    - init_db.sql (and init_test_db.sql for test environment)
    - init_security_db.sql (and init_security_test_db.sql for test environment)
    - run Junit class: UserServiceTest.java to insert users in test db. Please, to
      insert users in production db, open test.application.properties and modify
      db properties as here:
      spring.datasource.username=test_spring_db -> spring.datasource.username=spring_db
      spring.datasource.password=test_spring_db -> spring.datasource.password=spring_db


Security configuration
----------------------
Let's see the part of the application that concerns security (which is certainly the most interesting).
Two different SpringSecurity configurations have been implemented which are alternatives:

- basic configuration: any user is authorized to access the URLs of the rest exposed by the application (CompanyController, EmployeeController).
- JWT configuration: carries out Authentication and Authorization by implementing the OAuth 2.0 JWT (Json Web Token) standard which provides a validity token with a fixed expiry time.

Enabling the configuration is operated by the jwtSecurity property defined in the application properties (application.properties, property: spring.security.jwt).
Let's see the WebSecurityConfigurerAdapter SpringBoot configuration class:


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


The configuration that implements JWT includes an open logon rest url that implements the Authentication phase (class: UserController):

- verify the credentials passed in the logon POST header
- retrieves the roles assigned to the user
- retrieves permissions linked to roles
- generates and returns a validity token (JWT token, which contains the encrypted permissions of the user's role) with a fixed expiration time (read from the application properties file)

The token must be passed in the header of request calls to the protected rest urls (CompanyController,  EmployeeController), creating and enhancing the <Authorization> property with the token
returned from the logon rest.

The Authorization phase is managed by the JWTAuthorizationFilter filter inserted in the filter chain which operates in the following way:

- intercepts the URLs of the protected rests and checks the presence of the token (extracts the value of the <Authorization> property in the request header and checks its format)
- verifies the validity of the token and extracts the Claim;
- extracts the list of permissions from the Claim and enables access to the URL if the token's permissions include the permissions expected for that URL.

   For example the rest url:

        http://localhost:8092/springBootRest/companies

   provides permission:

        PermissionEnum.AUTH_COMPANY_READ

   as configured in WebSecurityConfigurerAdapter:

        .antMatchers(HttpMethod.GET,"/companies/**").hasAuthority(PermissionEnum.AUTH_COMPANY_READ.name())

   therefore, PermissionEnum.AUTH_COMPANY_READ must be present among the permissions encrypted in the token, otherwise the filter will return: 404 Access Forbidden

As mentioned, the authentication/authorization management uses the standard protocol JSON Web Token (JWT, see https://jwt.io/).
The authentication/authorization process is described in the Softtek article:

Token-based API authentication with Spring and JWT

The protocol includes:

- an authentication server that verifies the credentials and authenticates the client by issuing a validity token with a fixed time limit (JWT token)
- an authorization server which accepts the token for each protected request and confirms whether the client has permission to access that resource.
