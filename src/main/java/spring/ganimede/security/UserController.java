package spring.ganimede.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import spring.ganimede.logger.AppLogger;
import spring.ganimede.logger.AppLoggerService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;
import spring.ganimede.security.dao.UserService;
import spring.ganimede.security.entity.PermissionEnum;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
public class UserController
{
    private AppLogger logger = AppLoggerService.getLogger(UserController.class.getName());

    @Value("${spring.security.jwt.session-duration}")
    private Integer sessionDuration;

    SecretInfo secretInfo;
    @Autowired
    UserService userService;

    public UserController() {
        secretInfo = SecretInfo.getInstance();
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value="/logon", method = RequestMethod.POST, consumes = "application/json")
    public String logon(@Valid @RequestBody CredentialsDto credentials)
    {
        logger.info("User: " + credentials.getUser() + " - Try to login ...");
        return getJWTToken(credentials.getUser(), credentials.getPassword());
    }

    private String getJWTToken(String user, String password)
    {
        // Authentication server check credentials and get permissions of user's role
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(userService.getCommaSeparatedAuthorityList(user, password));
        // JWT token that will be generated it will authorize resources in these permission list (list of GrantedAuthority)
        String token = Jwts
                .builder()
                .setId(secretInfo.getTOKEN_ID())
                .setSubject(user)
                .claim("authorities",
                        grantedAuthorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + sessionDuration))
                .signWith(secretInfo.getSecretKey(), SignatureAlgorithm.HS512).compact();

        logger.info("User: " + user + " logged");
        return secretInfo.getTOKEN_PREFIX() + token;
    }

}
