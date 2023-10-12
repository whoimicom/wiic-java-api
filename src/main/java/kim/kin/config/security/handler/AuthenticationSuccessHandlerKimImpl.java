package kim.kin.config.security.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import kim.kin.common.ResultInfo;
import kim.kin.config.security.JwtTokenUtil;
import kim.kin.config.security.UserDetailsKimImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author kin.kim
 * @since 2023-10-12
 **/
@Component
public class AuthenticationSuccessHandlerKimImpl implements ServerAuthenticationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationSuccessHandlerKimImpl.class);
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private ObjectMapper objectMapper;


    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return Mono.defer(() -> Mono
                .just(webFilterExchange.getExchange().getResponse())
                .flatMap(response -> {
                    DataBufferFactory dataBufferFactory = response.bufferFactory();
                    UserDetailsKimImpl userDetails = (UserDetailsKimImpl) authentication.getDetails();
                    String username = userDetails.getUsername();

                    String bearer = jwtTokenUtil.generateToken(username, username, userDetails.getAuthorities());
                    userDetails.eraseCredentials();
                    userDetails.setBearer(bearer);
                    DataBuffer dataBuffer;
                    try {
                        dataBuffer = dataBufferFactory.wrap(objectMapper.writeValueAsBytes(ResultInfo.ok(userDetails)));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                        return Mono.error(new RuntimeException(e));
                    }
                    response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    response.getHeaders().set(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString());
                    response.setStatusCode(HttpStatus.OK);
                    return response.writeWith(Mono.just(dataBuffer));
                }));
    }
}
