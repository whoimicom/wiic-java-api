package kim.kin.config.swagger;

import kim.kin.config.security.AnonymousKimAccess;
import org.springframework.boot.SpringBootVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.*;

@EnableOpenApi
@Configuration
public class SwaggerConfiguration {
    private final SwaggerProperties swaggerProperties;

    public SwaggerConfiguration(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30).pathMapping("/")

                // 定义是否开启swagger，false为关闭，可以通过变量控制
                .enable(swaggerProperties.getEnable())
                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                // 接口调试地址
                .host(swaggerProperties.getTryHost())
                // 参数上有指定的注解时将整个参数不显示
                .ignoredParameterTypes()
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.any())
                // 不显示error开头的请求
                .paths(PathSelectors.regex("/error.*").negate())
//                .paths(PathSelectors.any())
                .build()
                // 支持的通讯协议集合
                .protocols(newHashSet("https", "http"))
                // 授权信息设置，必要的header token等认证信息
                .securitySchemes(securitySchemes())
                .tags(new Tag("authorization", "authorization"))
                // 授权信息全局应用
                .securityContexts(securityContexts());
    }

    /**
     * API 页面上半部分展示信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(swaggerProperties.getApplicationName() + " Api Doc")
                .description(swaggerProperties.getApplicationDescription())
                .contact(new Contact("kinkim", "https://kin.kim", "im@kin.kim"))
                .version("Application Version: " + swaggerProperties.getApplicationVersion() + ", Spring Boot Version: " + SpringBootVersion.getVersion())
                .build();
    }

    /**
     * 设置授权信息
     */
    private List<SecurityScheme> securitySchemes() {
//        ApiKey apiKey = new ApiKey("Authorization", "Bearer", In.HEADER.toValue());
//        return Collections.singletonList(apiKey);
        return Collections.singletonList(HttpAuthenticationScheme.JWT_BEARER_BUILDER
                .name("Authorization")
                .scheme("bearer")
                .build());
    }

    /**
     * 授权信息全局应用
     */
    private List<SecurityContext> securityContexts() {
//        return Collections.singletonList(
//                SecurityContext.builder()
//                        .securityReferences(Collections.singletonList(new SecurityReference("BASE_TOKEN", new AuthorizationScope[]{new AuthorizationScope("global", "")})))
//                        .build()
//        );
        SecurityContext context = new SecurityContext(
                defaultAuth(),
                // 配置需要访问授权的请求，效果是对应页面上的请求有没有小锁图标
                PathSelectors.regex("/auth.*").negate(),
                // 配置需要访问授权的请求，效果是对应页面上的请求有没有小锁图标
                each -> true,
                // operationSelector优先级高于上面两个，配置需要访问授权的请求，效果是对应页面上的请求有没有小锁图标
                // 将auth开头的请求和类、方法上有指定注解的请求在swagger页面上放行，不使用jwt bearer token 授权方案
                operationContext -> !operationContext.requestMappingPattern().matches("/auth.*") &&
                        operationContext.findControllerAnnotation(AnonymousKimAccess.class).isEmpty() &&
                        operationContext.findAnnotation(AnonymousKimAccess.class).isEmpty()
        );
        return Collections.singletonList(context);
    }

    /**
     * bearer
     *
     * @return List<SecurityReference>
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("Authorization", authorizationScopes));
    }

    @SafeVarargs
    private <T> Set<T> newHashSet(T... ts) {
        if (ts.length > 0) {
            return new LinkedHashSet<>(Arrays.asList(ts));
        }
        return null;
    }


}