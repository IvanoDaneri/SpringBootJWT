package spring.ganimede.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spring.ganimede.logger.AppLogger;
import spring.ganimede.logger.AppLoggerService;
import spring.ganimede.security.dao.UserService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtTokenService
{
    private final AppLogger logger = AppLoggerService.getLogger(JwtTokenService.class.getName());

    @Value("${spring.security.jwt.session-duration}")
    private Integer sessionDuration;

    SecretInfo secretInfo;

    @Autowired
    UserService userService;

    public JwtTokenService() {
        secretInfo = SecretInfo.getInstance();
    }

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public List<String> getPermissions(String token) {
        return getClaimFromToken(token, claims -> (List) claims.get(UserService.PERMISSIONS));
    }

    // Check token validity
    public Boolean validateToken(String token) {
        final String username = getUsernameFromToken(token);
        return username != null && !isTokenExpired(token);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // For retrieving any information from token we will need the secret key
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secretInfo.getSecretKey()).build().parseClaimsJws(token).getBody();
    }

    // Generate token for user.
    // While creating the token -
    // 1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    // 2. Sign the JWT using the HS512 algorithm and secret key.
    // 3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    public String generateToken(String user, String password)
    {
        logger.info("Try to generate Jwt token for user: " + user);

        // Authentication server check credentials and get permissions of user's role
        Map<String, Object> claims = userService.getPermissions(user, password);

        // JWT token that will be generated it will authorize resources in these permission list (list of GrantedAuthority)
        String token = Jwts
                .builder()
                .setClaims(claims)
                .setId(secretInfo.getTOKEN_ID())
                .setSubject(user)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + sessionDuration * 1000))
                .signWith(secretInfo.getSecretKey(), SignatureAlgorithm.HS512).compact();

        logger.info("Jwt token generated for user: " + user);

        return secretInfo.getTOKEN_PREFIX() + token;
    }

    // Check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

}
