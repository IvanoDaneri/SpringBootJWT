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

    private final String AUTHORIZATION_PROPERTY = "Authorization";
    private final String EMPTY_STRING = "";

    @Autowired
    JwtTokenService jwtTokenService;

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
                String jwtToken = validateToken(request);
                if(jwtToken != null)
                {
                    setUpSpringAuthentication(jwtToken);
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
        String jwtToken = request.getHeader(AUTHORIZATION_PROPERTY).replace(secretInfo.getTOKEN_PREFIX(), EMPTY_STRING);
        if(jwtTokenService.validateToken(jwtToken))
            return jwtToken;
        else
            return null;
    }

    private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse response)
    {
        String authenticationHeader = request.getHeader(AUTHORIZATION_PROPERTY);
        return authenticationHeader != null && authenticationHeader.startsWith(secretInfo.getTOKEN_PREFIX());
    }
}
