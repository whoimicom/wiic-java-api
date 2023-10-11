package kim.kin.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 授权逻辑处理中心
 */
@Component
public class AuthorizationManagerKimImpl implements ReactiveAuthorizationManager<AuthorizationContext> {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationManagerKimImpl.class);


    /**
     * @param authentication       the Authentication to check
     * @param authorizationContext the object to check
     * @return AuthorizationDecision
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {

        log.info("{}", authentication.toString());
        ServerWebExchange exchange = authorizationContext.getExchange();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        log.info(path);


        //通过角色判断
/*        List<String> accessibleRole = Arrays.asList("Admin", "Manager","Test");
        return authentication
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(auth -> {
                    log.debug(auth.getAuthorities().toString());
                    return auth.getAuthorities();
                })
                .map(GrantedAuthority::getAuthority)
                .any(accessibleRole::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));*/

        // 通过路径判断
        //                    System.out.println("authority"+authority);
        Mono<AuthorizationDecision> authorizationDecisionMono = authentication
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(auth -> {
                    log.info(auth.getAuthorities().toString());
                    return auth.getAuthorities();
                })
                .map(GrantedAuthority::getAuthority)
                .any(path::equals)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
        System.out.println(authorizationDecisionMono);
        return authorizationDecisionMono;

    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        return check(authentication, object)
                .filter(AuthorizationDecision::isGranted)
                .switchIfEmpty(Mono.defer(() -> {
//                    String body = JSONObject.toJSONString(ResultVO.error(SimpleResultEnum.PERMISSION_DENIED));
                    return Mono.error(new AccessDeniedException(""));
                }))
                .flatMap(d -> Mono.empty());
    }
}