package spring.ganimede.security;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.ganimede.logger.AppLogger;
import spring.ganimede.logger.AppLoggerService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JWTAuthorizationFilter extends OncePerRequestFilter
{
    private AppLogger logger = AppLoggerService.getLogger(JWTAuthorizationFilter.class.getName());

    private final String AUTHORIZATION_PROPERTY = "Authorization";
    private final String AUTHORITIES = "authorities";
    private final String EMPTY_STRING = "";

    SecretInfo secretInfo;

    public JWTAuthorizationFilter() {
        secretInfo = SecretInfo.getInstance();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        logger.info("JWT filter - Processing request ...");

        try
        {
            if(checkJWTToken(request, response))
            {
                Claims claims = validateToken(request);
                if(claims.get(AUTHORITIES) != null)
                {
                    setUpSpringAuthentication(claims);
                }
                else
                {
                    SecurityContextHolder.clearContext();
                }
            }
            else
            {
                SecurityContextHolder.clearContext();
            }

            // Method must come back to filter chain
            filterChain.doFilter(request, response);
            logger.info("JWT filter - Request processed");
        }
        catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e)
        {
            logger.info("JWT filter - Access forbidden");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private void setUpSpringAuthentication(Claims claims)
    {
        @SuppressWarnings("unchecked")
        List<HashMap<String, String>> authorities = (List) claims.get(AUTHORITIES);
        List<Collection<String>> grants = authorities.stream().map(HashMap::values).collect(Collectors.toList());
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        grants.forEach(strings -> strings.forEach(s -> grantedAuthorities.add(new SimpleGrantedAuthority(s))));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(),null,
                grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private Claims validateToken(HttpServletRequest request)
    {
        String jwtToken = request.getHeader(AUTHORIZATION_PROPERTY).replace(secretInfo.getTOKEN_PREFIX(), EMPTY_STRING);
        return Jwts.parserBuilder().setSigningKey(secretInfo.getSecretKey().getEncoded()).build().parseClaimsJws(jwtToken).getBody();
    }

    private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse response)
    {
        String authenticationHeader = request.getHeader(AUTHORIZATION_PROPERTY);
        if(authenticationHeader == null || !authenticationHeader.startsWith(secretInfo.getTOKEN_PREFIX()))
            return false;

        return true;
    }
}
