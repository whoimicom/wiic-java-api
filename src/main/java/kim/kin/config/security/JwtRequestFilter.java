package kim.kin.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import kim.kin.model.UserKimDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author choky
 */
//@Component
public class JwtRequestFilter extends BasicAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final JwtTokenUtil jwtTokenUtil;

    public JwtRequestFilter(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        super(authenticationManager);
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        final String authHeader = request.getHeader(SecurityKimParams.AUTH_KIM_HEADER);
        // 如果请求头中没有Authorization信息则直接放行了
        if (null == authHeader) {
            chain.doFilter(request, response);
            logger.warn("authHeader is null");
        } else {
            // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
            if (authHeader.startsWith(SecurityKimParams.AUTH_KIM_PREFIX)) {
                String jwtToken = authHeader.substring(SecurityKimParams.AUTH_KIM_PREFIX.length());
                try {
                    String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                    // Once we get the token validate it.
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        List<GrantedAuthority> authentication = jwtTokenUtil.getAuthentication(jwtToken);
                        UserKimDetails user = new UserKimDetails(username, authentication);
                        // if token is valid configure Spring Security to manually set authentication
                        if (jwtTokenUtil.validateToken(jwtToken, username)) {
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, authentication);
                            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            // After setting the Authentication in the context, we specify
                            // that the current user is authenticated. So it passes the
                            // Spring Security Configurations successfully.
                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        }
                    }
                    chain.doFilter(request, response);
//                    super.doFilterInternal(request, response, chain);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    System.out.println("Unable to get JWT Token");
                } catch (ExpiredJwtException e) {
                    e.printStackTrace();
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
        log.info("getPathInfo:{} getRequestURI:{} getRequestURL:{} getUserPrincipal:{} getServletPath:{}",
                request.getPathInfo(), request.getRequestURI(), request.getRequestURL(), request.getUserPrincipal(), request.getServletPath());
        String requestURI = request.getRequestURI();
        if (SecurityKimParams.LOGIN_URI.equals(requestURI)) {
            result = true;
        }
        if (SecurityKimParams.REGISTER_URI.equals(requestURI)) {
            result = true;
        }
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            result = true;
        }
        if ("/showReplicaStatus".equals(request.getServletPath())) {
            result = true;
        }
        return result;
    }

}
