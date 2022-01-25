package kim.kin.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import kim.kin.model.UserInfo;
import kim.kin.repository.UserInfoRepository;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

        final String authHeader = request.getHeader("Authorization");
        // 如果请求头中没有Authorization信息则直接放行了
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            logger.warn("JWT Token does not begin with Bearer String");
            return;
        }
        // 如果请求头中有token，则进行解析，并且设置认证信息


        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            List<GrantedAuthority> authentication = jwtTokenUtil.getAuthentication(jwtToken);
            User user = new User(username, "", authentication);
            // if token is valid configure Spring Security to manually set
            // authentication
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
//        chain.doFilter(request, response);
        super.doFilterInternal(request, response, chain);
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
