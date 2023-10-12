package kim.kin.config.security.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import kim.kin.common.ResultInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * @author kin.kim
 * @since 2023-10-12
 **/
@Component
public class AccessDeniedHandlerKimImpl implements ServerAccessDeniedHandler {
    private static final Logger log = LoggerFactory.getLogger(AccessDeniedHandlerKimImpl.class);
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {

        return Mono
                .defer(() -> Mono.just(exchange.getResponse()))
                .flatMap(response -> {
                    HttpStatus statusCode = HttpStatus.FORBIDDEN;
                    response.setStatusCode(statusCode);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    DataBufferFactory dataBufferFactory = response.bufferFactory();
                    String result;
                    try {
                        result = objectMapper.writeValueAsString(ResultInfo.fail(statusCode.value(), "AccessDeniedException"));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                        return Mono.error(new RuntimeException(e));
                    }
                    DataBuffer buffer = dataBufferFactory.wrap(result.getBytes(
                            Charset.defaultCharset()));
                    return response.writeWith(Mono.just(buffer));
                });
    }
}
