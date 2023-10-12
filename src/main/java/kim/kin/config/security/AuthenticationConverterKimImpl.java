package kim.kin.config.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerFormLoginAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
/**
 * @author kin.kim
 * @since 2023-10-12
 **/
@Component
public class AuthenticationConverterKimImpl extends ServerFormLoginAuthenticationConverter {
    private ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationConverterKimImpl.class);

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getRequest().getBody().next().flatMap(dataBuffer -> {
            InputStream inputStream = dataBuffer.asInputStream();
            try {
                Map<String, String> map = objectMapper.readValue(inputStream, new TypeReference<>() {
                });
                String accessChannel = Optional.ofNullable(map.get("accessChannel")).orElse("UNKNOWN_CHANNEL");
                String loginType = Optional.ofNullable(map.get("loginType")).orElse("UNKNOWN_LOGIN_TYPE");
                String username = map.get("username");
                String password = map.get("password");
                UserDetailsKimImpl userDetails = new UserDetailsKimImpl(username, password);
                userDetails.setAccessChannel(accessChannel);
                userDetails.setLoginType(loginType);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
                usernamePasswordAuthenticationToken.setDetails(userDetails);
                log.info(usernamePasswordAuthenticationToken.toString());
                return Mono.just(usernamePasswordAuthenticationToken);
            } catch (IOException e) {
                return Mono.error(new RuntimeException(e));
            }
        });
    }
}
