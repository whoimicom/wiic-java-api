package kim.kin.config.security;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * @author choky
 */
@Component
public class AuthenticationEntryPointKimImpl implements AuthenticationEntryPoint, Serializable {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationEntryPointKimImpl.class);
    @Serial
    private static final long serialVersionUID = -7858869558953243875L;

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.info("AuthenticationEntryPoint:{}", authException.getMessage());
        String authMsg = authException.getMessage();
        if (authException instanceof UsernameNotFoundException) {
            authMsg = "用户不存在！";
        } else if (authException instanceof BadCredentialsException) {
            authMsg = "用户名或密码错误！";
        } else if (authException instanceof LockedException) {
            authMsg = "用户已被锁定！";
        } else if (authException instanceof DisabledException) {
            authMsg = "用户不可用！";
        } else if (authException instanceof AccountExpiredException) {
            authMsg = "账户已过期！";
        } else if (authException instanceof CredentialsExpiredException) {
            authMsg = "用户密码已过期！";
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authMsg)));
//		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
