package kim.kin.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author choky
 */
@Component
public class JwtRequestFilter extends BasicAuthenticationFilter {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtRequestFilter(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        super(authenticationManager);
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(JwtTokenUtil.AUTH_KIM_HEADER);
        // 如果请求头中没有Authorization信息则直接放行了
        if (authHeader == null || !authHeader.startsWith(JwtTokenUtil.AUTH_KIM_TOKEN)) {
            chain.doFilter(request, response);
            logger.warn("JWT Token does not begin with Bearer String");
        } else {
            // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
            if (authHeader.startsWith(JwtTokenUtil.AUTH_KIM_TOKEN)) {
                String jwtToken = authHeader.substring(JwtTokenUtil.AUTH_KIM_TOKEN.length());
                try {
                    String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                    // Once we get the token validate it.
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        List<GrantedAuthority> authentication = jwtTokenUtil.getAuthentication(jwtToken);
                        User user = new User(username, "", authentication);
                        // if token is valid configure Spring Security to manually set authentication
                        if (jwtTokenUtil.validateToken(jwtToken, username)) {
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                    user, null, authentication);
                            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            // After setting the Authentication in the context, we specify
                            // that the current user is authenticated. So it passes the
                            // Spring Security Configurations successfully.
                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        }
                    }
//                  chain.doFilter(request, response);
                    super.doFilterInternal(request, response, chain);
                } catch (IllegalArgumentException e) {
                    System.out.println("Unable to get JWT Token");
                } catch (ExpiredJwtException e) {
                    System.out.println("JWT Token has expired");
                }
            } else {
                logger.warn("JWT Token does not begin with Bearer String");
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        boolean result = false;
        logger.info("getPathInfo:" + request.getPathInfo());
        logger.info("getRequestURI:" + request.getRequestURI());
        logger.info("getRequestURL:" + request.getRequestURL());
        logger.info("getUserPrincipal:" + request.getUserPrincipal());
        logger.info("getServletPath:" + request.getServletPath());
        String requestURI = request.getRequestURI();
        if (SecurityParams.LOGIN_URI.equals(requestURI)) {
            result = true;
        }
        if (SecurityParams.REGISTER_URI.equals(requestURI)) {
            result = true;
        }
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            result = true;
        }
        return result;
    }

}
