-----------------------------------
----------------------------------
SpringBootJwt 1.0

Arguments:
    - SpringBoot fundamentals
    - Entity persistence
    - Rest controller
    - JWT Security
----------------------------------
----------------------------------

----------------------------------
I) GENERAL APPLICATION DESCRIPTION
----------------------------------

----------------------------------
SPRINGBOOT FUNDAMENTALS
----------------------------------

SpringBoot is an open source framework that makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".
SpringBoot simplifies development and configuration of Spring application to create microservices and Web applications.
Any SpringBoot application has an application class annotated with @SpringBootApplication and a main method, for example in SpringBootJWT
here it is our application class:

@SpringBootApplication
public class SpringBootApp extends SpringBootServletInitializer
{
	private final AppLogger logger = AppLoggerService.getLogger( this.getClass().getName() );

	public static void main(String[] args)
	{
		SpringApplication.run( SpringBootApp.class, args );
	}	/* main */

    ...
}

@SpringBootApplication is a convenience annotation that adds some important features and enables the following annotations:

    - @Configuration: Tags the class as a source of bean definitions for the application context (in our case class WebSecurityConfig).
    - @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings,
      other beans, and various property settings. For example, if spring-webmvc is on the classpath, this annotation flags the application
      as a web application and activates key behaviors, such as setting up a DispatcherServlet.
    - @ComponentScan: Tells Spring to look for other components, configurations, and services in the com/example package, letting it find the controllers.

The main() method uses Spring Boot’s SpringApplication.run() method to launch an application.
Did you notice that there was not a single line of XML? There is no web.xml file, either.
This web application is 100% pure Java and you did not have to deal with configuring any plumbing or infrastructure.
The only configuration file is "application.properties" where we can specify:
    - application name
    - context path
    - server port

 The Spring Boot application will be available at the address:

    http://localhost:<server port>/<context path>

In order to create a deployable war file from Spring Boot application it's necessary to update your application’s main class to extend SpringBootServletInitializer
and override its configure method. This makes use of Spring Framework’s Servlet 3.0 support and allows you to configure your application when it’s launched
by the servlet container (for example Tomcat, Glassfish or JBoss).

----------------------------------
REST SERVICES
----------------------------------

SpringBootJWT exposes http rest GET and POST with two different SpringSecurity configurations that we will see later.
The application exposes two rest controllers that manage the CRUD of companies and employees entities (CompanyController, EmployeeController).
The backend app saves entities on a database, in our case OracleXE.
Db creation scripts and initial data population scripts are present for two different db users, test user and production user
(the junit tests have property files that map test user).

In the SpringBoot application:

- the Cross-Origin Resource Sharing (CORS) service has been disabled because it would have blocked POST type HTTP requests

- DTO validation on POST-type rests has been added, i.e.:
    - @NotEmpty and @NotNull annotations on the CompanyDto and EmployeeDto class fields
    - @Valid annotation on the rest method parameter

- we annotate some classes with @ControllerAdvice. These classes manage a series of exceptions thrown by the rest Controllers and
  which will be re-thrown to the rest client (classes: ControllerNotFoundAdvice, UserControllerAdvice)

Some rest url examples:

- curl to request all company records: curl http://localhost:8092/springBootRest/companies
- curl to request company record with id=1: curl http://localhost:8092/springBootRest/companies/1
- curl to request all employee record: curl http://localhost:8092/springBootRest/employees
- curl to request employee records with id=2: curl http://localhost:8092/springBootRest/employees/2


----------------------------------
APPLICATION TESTING
----------------------------------

Regarding Junit test of rest services (get and post):

- a specific SoapUI project has been released: springBootRestClient-soapui-project.xml
    - the client pass credentials to rest Logon to get valid JWT token from SpringBoot app
    - protected rests can be called using JWT token get from Logon

- Test java classes have been written for controllers (CompanyController, EmployeeController). Test classes performs CRUD entities by means of rest calls.
  CompanyControllerTest and EmployeeControllerTest inherits from Logon class that calls rest logon during startup and save JWT session token.
  CompanyControllerTest and EmployeeControllerTest set JWT session token in header of http request for rest calls.

- on controller tests the annotation used was:

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {SpringBootApp.class})

   This annotation launches SpringBootApp including rest services on the port configured in the properties file
   and therefore allows test classes to invoke the rest services using the RestTemplate factory and the getForObject methods for rest GET
   and postForObject for the rest POST (really in case of JWT configuration the exchange method is used because we have to pass the request with
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

Let's see the part of the application that concerns about security (which is certainly the most interesting part of app).
Two different SpringSecurity configurations have been implemented which are in alternative:

1) basic configuration: any user is authorized to access the URLs of the rest exposed by rest controllers (CompanyController, EmployeeController).
2) JWT configuration: carries out Authentication and Authorization by implementing the OAuth 2.0 JWT (Json Web Token) standard which provides
   a valid session token with a fixed expiry time.

Enable of security configuration 1 or 2 is operated by the jwtSecurity property defined in the application properties (application.properties, property: spring.security.jwt).
Valid session token duration is fixed by the sessionDuration property defined in the application properties (application.properties, property: spring.security.jwt.session-duration).

Let's see the WebSecurityConfig SpringBoot configuration class:

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    private static final RequestMatcher PROTECTED_URLS = new OrRequestMatcher(new AntPathRequestMatcher("/companies/**"), new AntPathRequestMatcher("/employees/**"));

    @Value("${spring.security.jwt}")
    private Boolean jwtSecurity;

    @Autowired
    JWTAuthorizationFilter jwtAuthorizationFilter;

    @Override
    public void configure( HttpSecurity http ) throws Exception
    {
        // This configuration enable CORS and disable CSRF for POST rest
        http.cors()
                .and()
                .csrf().ignoringAntMatchers("/logon", "/logoff", "/companies/addCompany", "/employees/addEmployee");

        // JWT security enabled
        if(jwtSecurity)
        {
            http.addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET,"/companies/**").hasAuthority(PermissionEnum.AUTH_COMPANY_READ.name())      // CompanyController GET must have AUTH_COMPANY_READ permission
                    .antMatchers(HttpMethod.POST,"/companies/**").hasAuthority(PermissionEnum.AUTH_COMPANY_ADD.name())      // CompanyController POST must have AUTH_COMPANY_ADD permission
                    .antMatchers(HttpMethod.GET,"/employees/**").hasAuthority(PermissionEnum.AUTH_EMPLOYEE_READ.name())     // EmployeeController GET must have AUTH_EMPLOYEE_READ permission
                    .antMatchers(HttpMethod.POST,"/employees/**").hasAuthority(PermissionEnum.AUTH_EMPLOYEE_ADD.name())     // EmployeeController POST must have AUTH_EMPLOYEE_ADD permission
                    .antMatchers(HttpMethod.POST, "/logon").permitAll()                                                 // Permit logon url to everyone to pass credentials and get JWT token
                    .antMatchers(HttpMethod.POST, "/logoff").permitAll()                                                 // Permit logon url to everyone to pass credentials and get JWT token
                    .requestMatchers(PROTECTED_URLS)                                                                            // These are urls protected by JWTAuthorizationFilter
                    .authenticated()
                    .and()
                    // This configuration disable all other default configurations
                    .formLogin().disable()
                    .httpBasic().disable()
                    .logout().disable();
        }
        // No security enabled for rest urls
        else
        {
            http.authorizeRequests()
                    // Add url exceptions to http security configuration to avoid http authentication for rest url path
                    .antMatchers("/companies/**", "/employees/**", "/simpleEmployees/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic();
        }
    }

}

1) Authentication phase.
The configuration that implements JWT includes a logon rest url (UserController.logon) that implements the Authentication phase (class: UserController):

- verify the credentials passed in http POST calling UserController.logon rest (*)
- retrieves the roles assigned to the user
- retrieves permissions linked to roles
- generates and returns back a validity token (JWT session token, which contains the encrypted permissions of the user's role) with a fixed expiration duration (red from the application properties file)

The token must be passed in the header of http request for rest calls to the protected rest urls (CompanyController,  EmployeeController), creating "Authorization" property and set value of property with token.
Example of token returned by logon method:
Bearer: eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJHYWxpbGVvSldUIiwic3ViIjoibXlBZG1pbiIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJBVVRIX0NPTVBBTllfQUREIn0seyJhdXRob3JpdHkiOiJBVVRIX0VNUExPWUVFX1JFQUQifSx7ImF1dGhvcml0eSI6IkFVVEhfQ09NUEFOWV9SRUFEIn0seyJhdXRob3JpdHkiOiJBVVRIX0VNUExPWUVFX0FERCJ9XSwiaWF0IjoxNzE5MjMxNDgxLCJleHAiOjE3MTkyMzIwODF9.Eo0wAQPqhi30uIo0Kzg50eCS8wWyEuSYtJPN03xgGSD-7aMsDaK3gDmEAqSEVurid1Xq8nYkiVasjeZuwT-WwQ

UserController provides also logoff method that adds jwt token in a black list (to avoid someone might use a valid token after the user
has logged off to his application).

(*) Remember that initial passwords saved on db are encrypted with Jasypt tool (by means of PBE_PASSWORD or ciphering password)

2) Authorization phase.
The Authorization phase is managed by the JWTAuthorizationFilter filter inserted in the filter chain which operates in the following way:

- intercepts the URLs of the protected rests and checks the presence of the token (extracts the value of the "Authorization" property in the request header and checks valid format)
- verifies the validity of the token (if token is not expired or is not in black list) and extracts the Claim;
- extracts the list of permissions from the Claim and enables URL request if the token's permissions include the permissions expected for that URL.

   For example the rest url:

        http://localhost:8092/springBootRest/companies

   provides permission:

        PermissionEnum.AUTH_COMPANY_READ

   as configured in WebSecurityConfig:

        .antMatchers(HttpMethod.GET,"/companies/**").hasAuthority(PermissionEnum.AUTH_COMPANY_READ.name())

   therefore, PermissionEnum.AUTH_COMPANY_READ must be present among the permissions encrypted in the token passed, otherwise the filter will return: 404 Access Forbidden

As mentioned, the authentication/authorization management uses the standard protocol JSON Web Token (JWT, see https://jwt.io/).
The authentication/authorization process is described in the Softtek article:

Token-based API authentication with Spring and JWT

The protocol includes:

- an Authentication Server that verifies the credentials and authenticates the client by generate a validity token with a fixed duration validity (JWT session token)
- an Authorization Server which accepts the token for each protected request and verify whether the client has permission to access that resource.

Another interesting article on JSON Web Token:

https://sopheamak.medium.com/springboot-how-to-invalidate-jwt-token-such-as-logout-or-reset-all-active-tokens-73f55289d47b

Generale articles on OAuth 2.0:

https://www.cybersecurity360.it/soluzioni-aziendali/oauth-2-0-cose-e-come-funziona-lo-standard-aperto-per-lautenticazione-sicura-online/
https://www.teranet.it/introduzione-ad-oauth-2

3) WebSecurityConfig must enable CORS. Why?
To make @CrossOrigin annotation work at controller level (classes annotated with @RestController) we need to explicitly enable CORS support at Spring Security level,
otherwise requests may be blocked by Spring Security (CORS violation) before reaching Spring MVC.
In addition, WebSecurityConfig disable CSRF for POST rest, otherwise client receive 403 error (access to resource forbidden) (1)
Here's our code:

        // This configuration enable CORS and disable CSRF for POST rest
        http.cors()
                .and()
                .csrf().ignoringAntMatchers("/logon", "/logoff", "/companies/addCompany", "/employees/addEmployee");

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
Security Benefit: To defend against CSRF, servers can use techniques like token-based protection (e.g., including a CSRF token in forms) or the “cookie-to-header” pattern (2).

In summary:

CORS deals with cross-origin requests and controls which domains can access resources.
CSRF is an attack that exploits a user’s session to perform unintended actions on their behalf.

Here's a detailed description about CORS:
https://aws.amazon.com/it/what-is/cross-origin-resource-sharing/

Here's a link about CORS support in Spring Framework:
https://spring.io/blog/2015/06/08/cors-support-in-spring-framework

(1) By default, Spring Security enables CSRF protection.
If the CSRF token is missing from the request header in PUT, POST, DELETE request, the server responds with a 403 error.
This behavior isn’t specific to any server environment, including localhost, staging, or production.
However, it’s important to note that disabling CSRF protection isn’t generally recommended in an application in production.
CSRF protection is a crucial security measure to prevent Cross-Site Forgery attacks.
Therefore, it’s advisable to include the CSRF token in the request header of state-changing operations (3).

(2) (3) In our case we disable CORS for application POST requests but, in this case, we use a JWT token to protect from Cross-Site Forgery attacks
(any POST requests must contain a valid JWT token in http request header).

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
