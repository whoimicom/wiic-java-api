package kim.kin.config.security.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;


public class EmailAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final Logger log = LoggerFactory.getLogger(EmailAuthenticationFilter.class);
    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";
    private final String AUTH_EMAIL_NAME = "email";
    private final String AUTH_EMAIL_CODE = "eCode";

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    /**
     * 是否 仅仅post方式
     */
    private boolean postOnly = true;

    /**
     * 通过 传入的 参数 创建 匹配器
     * 即 Filter过滤的url
     */
    public EmailAuthenticationFilter() {
        super(new AntPathRequestMatcher("/email/login", "POST"));
    }


    /**
     * filter 获得 用户名（邮箱） 和 密码（验证码） 装配到 token 上 ，
     * 然后把token 交给 provider 进行授权
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            EmailVo emailVo = obtainParam(request);
            EmailAuthenticationToken token = new EmailAuthenticationToken(emailVo.email, emailVo.eCode);
            this.setDetails(request, token);
            //交给 manager 发证
            return this.getAuthenticationManager().authenticate(token);
        }
    }

    /**
     * 获取 头部信息 让合适的provider 来验证他
     */
    public void setDetails(HttpServletRequest request, EmailAuthenticationToken token) {
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }


    public EmailVo obtainParam(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();
        EmailVo emailVo;
        if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)
                || APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(contentType)) {
            BufferedReader bufferedReader = request.getReader();
            StringBuilder stringBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputStr);
            }
            emailVo = new ObjectMapper().readValue(stringBuilder.toString(), EmailVo.class);

        } else {
            emailVo = new EmailVo(request.getParameter(AUTH_EMAIL_NAME), request.getParameter(AUTH_EMAIL_CODE));
        }
        log.info(String.valueOf(emailVo));
        return emailVo;
    }

    record EmailVo(String email, String eCode) implements Serializable {
    }

}
