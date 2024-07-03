package spring.ganimede.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.ganimede.logger.AppLogger;
import spring.ganimede.logger.AppLoggerService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter
{
    private final AppLogger logger = AppLoggerService.getLogger(JWTAuthorizationFilter.class.getName());

    public static final String AUTHORIZATION_PROPERTY = "Authorization";
    public static final String EMPTY_STRING = "";

    @Autowired
    JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        logger.info("JWT filter - Processing request ...");

        try
        {
            // Check if token is present
            if(checkJWTToken(request, response))
            {
                // Check if token is valid
                String jwtToken = validateToken(request);
                if(jwtToken != null)
                {
                    // Check if token is in black list (user logged out)
                    if(!isTokenBlocked(jwtToken))
                    {
                        logger.info("JWT session present");
                        setUpSpringAuthentication(jwtToken);
                    }
                    else
                    {
                        logger.info("JWT token is in black list (user logged out)");
                        SecurityContextHolder.clearContext();
                    }
                }
                else
                {
                    logger.info("JWT session expired");
                    SecurityContextHolder.clearContext();
                }
            }
            else
            {
                logger.info("No JWT session present");
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

    private void setUpSpringAuthentication(String jwtToken)
    {
        @SuppressWarnings("unchecked")
        List<String> authorities = jwtTokenService.getPermissions(jwtToken);
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        authorities.forEach(authority -> grantedAuthorities.add(new SimpleGrantedAuthority(authority)));
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(jwtTokenService.getUsernameFromToken(jwtToken),null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String validateToken(HttpServletRequest request)
    {
        String jwtToken = request.getHeader(AUTHORIZATION_PROPERTY).replace(SecretInfo.TOKEN_PREFIX, EMPTY_STRING);
        if(jwtTokenService.validateToken(jwtToken))
            return jwtToken;
        else
            return null;
    }

    private boolean isTokenBlocked(String jwtToken)
    {
        return jwtTokenService.isTokenInBlackList(jwtToken);
    }

    private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse response)
    {
        String authenticationHeader = request.getHeader(AUTHORIZATION_PROPERTY);
        return authenticationHeader != null && authenticationHeader.startsWith(SecretInfo.TOKEN_PREFIX);
    }
}
