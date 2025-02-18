package spring.ganimede.security;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import spring.ganimede.logger.AppLogger;
import spring.ganimede.logger.AppLoggerService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;
import spring.ganimede.security.dao.UserRepository;
import spring.ganimede.security.dao.UserService;
import spring.ganimede.security.entity.PermissionEnum;
import spring.ganimede.security.entity.Role;
import spring.ganimede.security.entity.User;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
public class UserController
{
    private final AppLogger logger = AppLoggerService.getLogger(UserController.class.getName());

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    UserRepository userRepository;


    @CrossOrigin(origins = "http://localhost:4200") // CORS enabled for origin: http://localhost:4200
    @RequestMapping(value="/logon", method = RequestMethod.POST, consumes = "application/json")
    public SessionDto logon(@Valid @RequestBody CredentialsDto credentials)
    {
        logger.info("User: " + credentials.getUser() + " - Try to login ...");
        String token = jwtTokenService.generateToken(credentials.getUser(), credentials.getPassword());
        List<User> users = userRepository.findByName(credentials.getUser());
        User user = users.stream().findFirst().get();
        // We simplify user management getting only first user's role
        Role role = user.getRoles().stream().findFirst().get();
        logger.info("User " + user + " logged");
        return new SessionDto(user.getName(), role.getName(), token);
    }

    @CrossOrigin(origins = "http://localhost:4200") // CORS enabled for origin: http://localhost:4200
    @RequestMapping(value="/logoff", method = RequestMethod.POST, consumes = "application/json")
    public void logoff(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationProp)
    {
        logger.info("Logoff user");
        String token = authorizationProp.replace(SecretInfo.TOKEN_PREFIX, JWTAuthorizationFilter.EMPTY_STRING);
        jwtTokenService.addTokenToBlackList(token);
    }

}
