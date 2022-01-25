package kim.kin.config.security;

import kim.kin.config.security.email.EmailCodeAuthenticationFilter;
import kim.kin.config.security.email.EmailCodeAuthenticationProvider;
import kim.kin.config.security.handler.AuthenticationFailureKimImpl;
import kim.kin.config.security.handler.AuthenticationSuccessKimImpl;
import kim.kin.config.security.user.UserDetailsServiceImpl;
import kim.kin.config.security.user.UsernamePasswordKimFilter;
import kim.kin.repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

/**
 * @author choky
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigurerKimAdapter extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfigurerKimAdapter.class);
    private final AuthenticationEntryPointKimImpl authenticationEntryPointKimImpl;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    //    private final JwtRequestFilter jwtRequestFilter;
    private final AccessDeniedKimImpl accessDeniedKimImpl;
    private final AuthenticationFailureKimImpl authenticationFailureKimImpl;
    private final AuthenticationSuccessKimImpl authenticationSuccessKimImpl;
    private final JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserInfoRepository userInfoRepository;

    public WebSecurityConfigurerKimAdapter(AuthenticationEntryPointKimImpl authenticationEntryPointKimImpl, UserDetailsServiceImpl userDetailsServiceImpl, AccessDeniedKimImpl accessDeniedKimImpl, AuthenticationFailureKimImpl authenticationFailureKimImpl, AuthenticationSuccessKimImpl authenticationSuccessKimImpl, JwtTokenUtil jwtTokenUtil) {
        this.authenticationEntryPointKimImpl = authenticationEntryPointKimImpl;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
//        this.jwtRequestFilter = jwtRequestFilter;
        this.accessDeniedKimImpl = accessDeniedKimImpl;
        this.authenticationFailureKimImpl = authenticationFailureKimImpl;
        this.authenticationSuccessKimImpl = authenticationSuccessKimImpl;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /*
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
    } */
    @Bean
    public EmailCodeAuthenticationFilter emailCodeAuthenticationFilter() {
        EmailCodeAuthenticationFilter emailCodeAuthenticationFilter = new EmailCodeAuthenticationFilter();
        emailCodeAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessKimImpl);
        emailCodeAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureKimImpl);
        return emailCodeAuthenticationFilter;
    }

    @Bean
    public EmailCodeAuthenticationProvider emailCodeAuthenticationProvider() {
        return new EmailCodeAuthenticationProvider(userInfoRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
        auth.authenticationProvider(emailCodeAuthenticationProvider());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * hasRole([role])	用户拥有指定的角色时返回true(hasRole()默认会将配置中的 role 带有 ROLE_ 前缀再和用户的角色权限 进行对比)
     * hasAnyRole([role1,role2])	用户拥有任意一个指定中的角色时返回true
     * hasAuthority([auth])	同hasRole()但不添加前缀 ROLE_
     * hasAnyAuthority([auth1,auth2])	同hasAnyRole([auth1,auth2])，但不添加前缀 ROLE_
     * permitAll	永远返回true
     * denyAll	永远返回false
     * anonymous	当前用户时 anonymous(匿名、未认证)时返回true
     * rememberMe	当前用户时 rememberMe(记住登录) 时发挥true
     * authentication	当前登录用户的 authentication 对象
     * fullAuthticated	当前用户既不是 anonymous 也不是 rememberMe 时返回true（即正常认证登录时返回true）
     * hasIpAddress("192.168.1.0/24")	ip匹配时返回true
     * openidLogin()	用于基于 OpenId 的验证
     * headers()	将安全标头添加到响应
     * cors()	配置跨域资源共享（ CORS ）
     * sessionManagement()	允许配置会话管理
     * portMapper()	允许配置一个PortMapper(HttpSecurity#(getSharedObject(class)))，其他提供SecurityConfigurer的对象使用 PortMapper 从 HTTP 重定向到 HTTPS 或者从 HTTPS 重定向到 HTTP。默认情况下，Spring Security使用一个PortMapperImpl映射 HTTP 端口8080到 HTTPS 端口8443，HTTP 端口80到 HTTPS 端口443
     * jee()	配置基于容器的预认证。 在这种情况下，认证由Servlet容器管理
     * x509()	配置基于x509的认证
     * rememberMe	允许配置“记住我”的验证
     * authorizeRequests()	允许基于使用HttpServletRequest限制访问
     * requestCache()	允许配置请求缓存
     * exceptionHandling()	允许配置错误处理
     * securityContext()	在HttpServletRequests之间的SecurityContextHolder上设置SecurityContext的管理。 当使用WebSecurityConfigurerAdapter时，这将自动应用
     * servletApi()	将HttpServletRequest方法与在其上找到的值集成到SecurityContext中。 当使用WebSecurityConfigurerAdapter时，这将自动应用
     * csrf()	添加 CSRF 支持，使用WebSecurityConfigurerAdapter时，默认启用
     * logout()	添加退出登录支持。当使用WebSecurityConfigurerAdapter时，这将自动应用。默认情况是，访问URL"/ logout"，使HTTP Session无效来清除用户，清除已配置的任何#rememberMe()身份验证，清除SecurityContextHolder，然后重定向到"/login?success"
     * anonymous()	允许配置匿名用户的表示方法。 当与WebSecurityConfigurerAdapter结合使用时，这将自动应用。 默认情况下，匿名用户将使用org.springframework.security.authentication.AnonymousAuthenticationToken表示，并包含角色 "ROLE_ANONYMOUS"
     * formLogin()	指定支持基于表单的身份验证。如果未指定FormLoginConfigurer#loginPage(String)，则将生成默认登录页面
     * oauth2Login()	根据外部OAuth 2.0或OpenID Connect 1.0提供程序配置身份验证
     * requiresChannel()	配置通道安全。为了使该配置有用，必须提供至少一个到所需信道的映射
     * httpBasic()	配置 Http Basic 验证
     * addFilterAt()	在指定的Filter类的位置添加过滤器
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = getApplicationContext().getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class).getHandlerMethods();
        Map<String, Set<String>> anonymousUrls = anonymousUrls(handlerMethodMap);

        httpSecurity.authenticationProvider(emailCodeAuthenticationProvider())
                .addFilterBefore(emailCodeAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtRequestFilter(authenticationManager(), jwtTokenUtil),UsernamePasswordAuthenticationFilter.class)
                .addFilter(new UsernamePasswordKimFilter(authenticationManager(), jwtTokenUtil));
        // We don't need CSRF for this example
        httpSecurity.csrf().disable()
                // dont authenticate this particular request
                .authorizeRequests().antMatchers("/authenticate", "/register", "/").permitAll()
                // swagger
                .antMatchers("/swagger**/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/v3/**").permitAll()
                .antMatchers("/doc.html").permitAll()

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
