package spring.ganimede.security;

import spring.ganimede.logger.AppLogger;
import spring.ganimede.logger.AppLoggerService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
public class UserController
{
    private AppLogger logger = AppLoggerService.getLogger(UserController.class.getName());

    private static final Integer SESSION_DURATION = 600000;

    SecretInfo secretInfo;

    public UserController() {
        secretInfo = SecretInfo.getInstance();
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value="/logon", method = RequestMethod.POST, consumes = "application/json")
    public String logon(@Valid @RequestBody CredentialsDto credentials)
    {
        logger.info("User: " + credentials.getUser() + " try to login");
        return getJWTToken(credentials.getUser());
    }

    private String getJWTToken(String user)
    {
        // TODO: Authentication server must check credentials and get permissions of user's role

        // JWT token that will be generated it will authorize resources in these permission list (list of GrantedAuthority)
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(Permission.getCommaSeparatedAuthorityList());
        String token = Jwts
                .builder()
                .setId(secretInfo.getTOKEN_ID())
                .setSubject(user)
                .claim("authorities",
                        grantedAuthorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + SESSION_DURATION))
                .signWith(secretInfo.getSecretKey(), SignatureAlgorithm.HS512).compact();

        return secretInfo.getTOKEN_PREFIX() + token;
    }

}
