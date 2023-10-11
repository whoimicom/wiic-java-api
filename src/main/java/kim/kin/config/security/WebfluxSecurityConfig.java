package kim.kin.config.security;

import jakarta.annotation.Resource;
import kim.kin.config.security.handler.AccessDeniedHandlerKimImpl;
import kim.kin.config.security.handler.AuthenticationFailureHandlerKimImpl;
import kim.kin.config.security.handler.AuthenticationSuccessHandlerKimImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebfluxSecurityConfig {
    @Resource
    private AuthenticationConverterKimImpl authenticationConverter;

    @Resource
    private AuthorizationManagerKimImpl authorizationManager;
    @Resource
    private AuthenticationManagerKimImpl authenticationManager;

    @Resource
    private AuthenticationSuccessHandlerKimImpl authenticationSuccessHandler;
    @Resource
    private SecurityContextRepositoryKimImpl securityContextRepository;

    @Resource
    private AuthenticationFailureHandlerKimImpl authenticationFailureHandler;
    @Resource
    private AccessDeniedHandlerKimImpl accessDeniedHandler;
    @Resource
    private ApplicationContext applicationContext;

/*    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("user")
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }*/

    /**
     * for all
     *
     * @param serverHttpSecurity serverHttpSecurity
     * @return SecurityWebFilterChain
     */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public SecurityWebFilterChain appFilterChain(ServerHttpSecurity serverHttpSecurity) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class).getHandlerMethods();
        Set<String> anonymousUrls = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();
            AnonymousKimAccess anonymousKimAccess = handlerMethod.getMethodAnnotation(AnonymousKimAccess.class);
            if (null != anonymousKimAccess) {
                Set<String> path = infoEntry.getKey().getPatternsCondition().getDirectPaths();
                anonymousUrls.addAll(path);
                path = path.stream().map(str -> "/gateway-api" + str).collect(Collectors.toSet());
                anonymousUrls.addAll(path);
            }
        }

        serverHttpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(securityContextRepository)
//                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/app/**"))
                .authorizeExchange(exchanges -> {
//                            exchanges.anyExchange().authenticated();
//                             exchange.pathMatchers(pattern).permitAll()
                            // 拦截认证
                            if (!anonymousUrls.isEmpty()) {
                                exchanges.pathMatchers(anonymousUrls.toArray(new String[0])).permitAll();
                            }
                            exchanges.pathMatchers(HttpMethod.OPTIONS).permitAll().anyExchange().access(authorizationManager);
                        }
                )
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.accessDeniedHandler(accessDeniedHandler))
//                .exceptionHandling().accessDeniedHandler(accessDeniedHandler).and()
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(withDefaults())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        return serverHttpSecurity.build();
    }

    private AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(reactiveAuthenticationManager());

        filter.setSecurityContextRepository(securityContextRepository);
        filter.setServerAuthenticationConverter(authenticationConverter);
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        filter.setRequiresAuthenticationMatcher(
                pathMatchers(HttpMethod.POST, "/auth/login")
        );
        return filter;
    }

    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager() {
        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
        managers.add(authenticationManager);
        return new DelegatingReactiveAuthenticationManager(managers);
    }


    /**
     * 用于WEB
     *
     * @param http ServerHttpSecurity
     * @return SecurityWebFilterChain
     */
    @Bean
    SecurityWebFilterChain webHttpSecurity(ServerHttpSecurity http) {
        http
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/web/**"))
                .authorizeExchange((exchanges) -> exchanges
                        .anyExchange().authenticated()
                )
                .httpBasic(withDefaults());
        return http.build();
    }
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}