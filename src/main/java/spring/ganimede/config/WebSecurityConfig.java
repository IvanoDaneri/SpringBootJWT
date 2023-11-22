package spring.ganimede.config;

import spring.ganimede.security.JWTAuthorizationFilter;
import spring.ganimede.security.entity.PermissionEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

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
                    .antMatchers(HttpMethod.POST, "/logon").permitAll()                                                 // Permit logon url to everyone to pass credentials and get JWT token
                    .requestMatchers(PROTECTED_URLS)                                                                               // These are urls protected by JWTAuthorizationFilter
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
