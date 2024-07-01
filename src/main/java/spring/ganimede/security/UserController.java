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
    private final AppLogger logger = AppLoggerService.getLogger(UserController.class.getName());

    @Autowired
    JwtTokenService jwtTokenService;


    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value="/logon", method = RequestMethod.POST, consumes = "application/json")
    public String logon(@Valid @RequestBody CredentialsDto credentials)
    {
        logger.info("User: " + credentials.getUser() + " - Try to login ...");
        String token = jwtTokenService.generateToken(credentials.getUser(), credentials.getPassword());
        logger.info("User " + credentials.getUser() + " logged");
        return token;
    }

}
