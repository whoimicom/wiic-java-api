package kim.kin.config.security;

import kim.kin.config.security.email.EmailAuthenticationFilter;
import kim.kin.config.security.email.EmailAuthenticationProvider;
import kim.kin.config.security.handler.AuthenticationFailureKimImpl;
import kim.kin.config.security.handler.AuthenticationSuccessKimImpl;
import kim.kin.config.security.user.UserDetailsServiceImpl;
import kim.kin.config.security.user.UsernamePasswordKimFilter;
import kim.kin.repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

/**
 * @author choky
 * @see <a href="https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter">spring-security-without-the-websecurityconfigureradapter</a>
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigurerKim {
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfigurerKim.class);
    private final AuthenticationEntryPointKimImpl authenticationEntryPointKimImpl;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final AccessDeniedKimImpl accessDeniedKimImpl;
    private final AuthenticationFailureKimImpl authenticationFailureKimImpl;
    private final AuthenticationSuccessKimImpl authenticationSuccessKimImpl;
    private final JwtTokenUtil jwtTokenUtil;
    private final ApplicationContext applicationContext;

    private UserInfoRepository userInfoRepository;

    @Autowired
    public void setUserInfoRepository(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public WebSecurityConfigurerKim(AuthenticationEntryPointKimImpl authenticationEntryPointKimImpl, UserDetailsServiceImpl userDetailsServiceImpl, AccessDeniedKimImpl accessDeniedKimImpl, AuthenticationFailureKimImpl authenticationFailureKimImpl, AuthenticationSuccessKimImpl authenticationSuccessKimImpl, JwtTokenUtil jwtTokenUtil, ApplicationContext applicationContext) {
        this.authenticationEntryPointKimImpl = authenticationEntryPointKimImpl;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.accessDeniedKimImpl = accessDeniedKimImpl;
        this.authenticationFailureKimImpl = authenticationFailureKimImpl;
        this.authenticationSuccessKimImpl = authenticationSuccessKimImpl;
        this.jwtTokenUtil = jwtTokenUtil;
        this.applicationContext = applicationContext;
    }

    @Bean
    public EmailAuthenticationFilter emailAuthenticationFilter() {
        EmailAuthenticationFilter emailAuthenticationFilter = new EmailAuthenticationFilter();
        emailAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessKimImpl);
        emailAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureKimImpl);
        return emailAuthenticationFilter;
    }

    @Bean
    public EmailAuthenticationProvider emailAuthenticationProvider() {
        return new EmailAuthenticationProvider(userInfoRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    //    @Bean//刷新token时自动调用
//    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
//        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
//        provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsServiceImpl));
//        return provider;
//    }
    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsServiceImpl);
        return daoAuthenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager() {
        ProviderManager providerManager = new ProviderManager(Arrays.asList(emailAuthenticationProvider(), daoAuthenticationProvider()));
        //不擦除认证密码，擦除会导致TokenBasedRememberMeServices因为找不到Credentials再调用UserDetailsService而抛出UsernameNotFoundException
//        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/register", "/")
                // swagger3
                .antMatchers("/swagger**/**")
                .antMatchers("/webjars/**")
                .antMatchers("/v3/**")
                .antMatchers("/doc.html");
    }

    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class).getHandlerMethods();
        Map<String, Set<String>> anonymousUrls = anonymousUrls(handlerMethodMap);
        httpSecurity
//                .authenticationProvider(emailAuthenticationProvider())
//                .authenticationManager(authenticationManager)
                .addFilterBefore(emailAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtRequestFilter(authenticationManager, jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilter(new UsernamePasswordKimFilter(authenticationManager, jwtTokenUtil));
        // We don't need CSRF for this example
        httpSecurity.csrf().disable()
                .authorizeRequests()
                // permitAll OPTIONS
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.GET, anonymousUrls.get(HttpMethod.GET.toString()).toArray(String[]::new)).permitAll()
                .antMatchers(HttpMethod.POST, anonymousUrls.get(HttpMethod.POST.toString()).toArray(String[]::new)).permitAll()
                .antMatchers(HttpMethod.PUT, anonymousUrls.get(HttpMethod.PUT.toString()).toArray(String[]::new)).permitAll()
                .antMatchers(HttpMethod.PATCH, anonymousUrls.get(HttpMethod.PATCH.toString()).toArray(String[]::new)).permitAll()
                .antMatchers(HttpMethod.DELETE, anonymousUrls.get(HttpMethod.DELETE.toString()).toArray(String[]::new)).permitAll()
                .antMatchers(anonymousUrls.get("ALL").toArray(String[]::new)).permitAll()
                // all other requests need to be authenticated
                .anyRequest().authenticated().and()
                // make sure we use stateless session; session won't be used to
                // store user's state.
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPointKimImpl)
                .accessDeniedHandler(accessDeniedKimImpl)
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return httpSecurity.build();

        // Add a filter to validate the tokens with every request
//        httpSecurity.addFilterBefore(new JwtRequestFilter(authenticationManager(), jwtTokenUtil), UsernamePasswordAuthenticationFilter.class);
//        httpSecurity.addFilter(new JwtRequestFilter(authenticationManager(), jwtTokenUtil));
    }

    private Map<String, Set<String>> anonymousUrls(Map<RequestMappingInfo, HandlerMethod> handlerMethodMap) {
        Map<String, Set<String>> anonymousUrls = new HashMap<>(6);
        Set<String> get = new HashSet<>();
        Set<String> post = new HashSet<>();
        Set<String> put = new HashSet<>();
        Set<String> patch = new HashSet<>();
        Set<String> delete = new HashSet<>();
        Set<String> all = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();
            AnonymousKimAccess anonymousKimAccess = handlerMethod.getMethodAnnotation(AnonymousKimAccess.class);
            if (null != anonymousKimAccess) {
                List<RequestMethod> requestMethods = new ArrayList<>(infoEntry.getKey().getMethodsCondition().getMethods());
                if (0 != requestMethods.size()) {
                    HttpMethod httpMethod = HttpMethod.resolve(requestMethods.get(0).name());
                    switch (Objects.requireNonNull(httpMethod)) {
                        case GET -> {
                            assert infoEntry.getKey().getPatternsCondition() != null;
                            get.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
                        }
                        case POST -> {
                            assert infoEntry.getKey().getPatternsCondition() != null;
                            post.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
                        }
                        case PUT -> {
                            assert infoEntry.getKey().getPatternsCondition() != null;
                            put.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
                        }
                        case PATCH -> {
                            assert infoEntry.getKey().getPatternsCondition() != null;
                            patch.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
                        }
                        case DELETE -> {
                            assert infoEntry.getKey().getPatternsCondition() != null;
                            delete.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
                        }
                        default -> {
                        }
                    }
                } else {
                    assert infoEntry.getKey().getPatternsCondition() != null;
                    all.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
                }

            }
        }
        anonymousUrls.put(HttpMethod.GET.toString(), get);
        anonymousUrls.put(HttpMethod.POST.toString(), post);
        anonymousUrls.put(HttpMethod.PUT.toString(), put);
        anonymousUrls.put(HttpMethod.PATCH.toString(), patch);
        anonymousUrls.put(HttpMethod.DELETE.toString(), delete);
        anonymousUrls.put("ALL", all);
        logger.info(String.valueOf(anonymousUrls));
        logger.info(Arrays.toString(anonymousUrls.get("ALL").toArray(String[]::new)));
        return anonymousUrls;
    }
}
