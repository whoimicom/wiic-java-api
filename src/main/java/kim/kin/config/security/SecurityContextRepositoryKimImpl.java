package kim.kin.config.security;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * 从 token 中提取用户凭证
 *
 * @author kin.kim
 * @since 2023-10-12
 **/
@Component
public class SecurityContextRepositoryKimImpl implements ServerSecurityContextRepository {
    private static final Logger log = LoggerFactory.getLogger(SecurityContextRepositoryKimImpl.class);
    @Resource
    private AuthenticationManagerKimImpl authenticationManager;

    @Resource
    private JwtTokenUtil jwtTokenUtil;


    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {

        log.debug("{}", exchange.toString());

        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
        String authorization = httpHeaders.getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || authorization.isEmpty()) {
            return Mono.empty();
        }

        String token = authorization.substring(JwtTokenUtil.AUTH_BEARER.length());
        log.info("token: {}", token);
        if (token.isEmpty()) {
            return Mono.empty();
        }

        String username = jwtTokenUtil.getUsernameFromToken(token);
        jwtTokenUtil.validateToken(token, username);
        String userId = jwtTokenUtil.getIdFromToken(token);
        List<GrantedAuthority> authentication = jwtTokenUtil.getAuthentication(token);


        // 构建用户令牌
//        MyUserDetails myUserDetails = new MyUserDetails();
//        myUserDetails.setId(userId);
//        myUserDetails.setUsername(username);
//        myUserDetails.setRoleList(list);


        // 构建 Security 的认证凭据
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, authentication);
        log.debug("从 token 中解析出的用户信息：{}", usernamePasswordAuthenticationToken);

        // remove Token
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(JwtTokenUtil.AUTH_USER_ID, userId)
                .header(JwtTokenUtil.AUTH_USERNAME, username)
                .header(JwtTokenUtil.AUTH_AUTHORITIES, authorization)
                .headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION))
                .build();
        exchange.mutate().request(request).build();
//        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        return authenticationManager
                .authenticate(usernamePasswordAuthenticationToken)
                .map(SecurityContextImpl::new);
    }
}
