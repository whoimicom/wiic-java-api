package kim.kin.config.security.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import kim.kin.common.ResultInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class AuthenticationFailureHandlerKimImpl implements ServerAuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFailureHandlerKimImpl.class);
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return Mono.defer(() -> Mono.just(webFilterExchange.getExchange().getResponse()).flatMap(response -> {
            HttpStatus statusCode = HttpStatus.UNAUTHORIZED;
            log.debug(response.toString());

            DataBufferFactory dataBufferFactory = response.bufferFactory();
            ResultInfo<String> resultInfo = ResultInfo.fail(HttpStatus.UNAUTHORIZED.value(), "onAuthenticationFailure");
            // 账号不存在
            if (exception instanceof UsernameNotFoundException) {
                resultInfo.setResultMsg("UsernameNotFoundException");
                // 用户名或密码错误
            } else if (exception instanceof BadCredentialsException) {
                resultInfo.setResultMsg("BadCredentialsException");
                // 账号已过期
            } else if (exception instanceof AccountExpiredException) {
                resultInfo.setResultMsg("AccountExpiredException");
                // 账号已被锁定
            } else if (exception instanceof LockedException) {
                resultInfo.setResultMsg("LockedException");
                // 用户凭证已失效
            } else if (exception instanceof CredentialsExpiredException) {
                resultInfo.setResultMsg("CredentialsExpiredException");
                // 账号已被禁用
            } else if (exception instanceof DisabledException) {
                resultInfo.setResultMsg("DisabledException");
            } else if (exception instanceof AuthenticationServiceException) {
                resultInfo.setResultMsg("AuthenticationServiceException");
            }

            byte[] bytes;
            try {
                bytes = objectMapper.writeValueAsBytes(resultInfo);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
                return Mono.error(new RuntimeException(e));
            }
            DataBuffer dataBuffer = dataBufferFactory.wrap(bytes);

            response.setStatusCode(statusCode);
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return response.writeWith(Mono.just(dataBuffer));
        }));
    }
}
