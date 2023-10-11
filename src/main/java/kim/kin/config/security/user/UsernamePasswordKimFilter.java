package kim.kin.config.security.user;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kim.kin.config.security.handler.JwtTokenUtil;
import kim.kin.config.security.handler.SecurityKimParams;
import kim.kin.model.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.*;

public class UsernamePasswordKimFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(UsernamePasswordKimFilter.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public UsernamePasswordKimFilter(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    /**
     * 执行实际的身份验证。
     * 该实现应执行以下操作之一：
     * 返回已验证用户的已填充验证令牌，指示验证成功
     * 返回null，表示身份验证过程仍在进行中。 在返回之前，实现应执行完成该过程所需的任何其他工作。
     * 如果身份验证过程失败，则抛出AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //输入流中获取到登录的信息
        try {
            UserInfoRecord userInfoRecord = this.obtainParam(request);
            String username = userInfoRecord.username;
            String password = userInfoRecord.password;
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
            return authenticationManager.authenticate(authentication);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AuthenticationServiceException(e.getMessage());
        }
    }

    public UserInfoRecord obtainParam(HttpServletRequest request) throws IOException {
        String contentType = Optional.ofNullable(request.getContentType()).orElse("");
        UserInfoRecord userInfoRecord;
        if (contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE)) {
            try (BufferedReader bufferedReader = request.getReader()) {
                StringBuilder stringBuilder = new StringBuilder();
                String inputStr;
                while ((inputStr = bufferedReader.readLine()) != null) {
                    stringBuilder.append(inputStr);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                userInfoRecord = objectMapper.readValue(stringBuilder.toString(), UserInfoRecord.class);
            }
        } else {
            String AUTH_EMAIL_NAME = "username";
            String AUTH_EMAIL_CODE = "password";
            userInfoRecord = new UserInfoRecord(request.getParameter(AUTH_EMAIL_NAME), request.getParameter(AUTH_EMAIL_CODE));
        }
        log.info(String.valueOf(userInfoRecord));
        return userInfoRecord;
    }

    record UserInfoRecord(String username, String password) implements Serializable {
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {

        Object principal = authResult.getPrincipal();
        User user = (User) principal;
//        List<String> roles = new ArrayList<>();
        // 因为在JwtUser中存了权限信息，可以直接获取，由于只有一个角色就这么干了
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
/*        for (GrantedAuthority authority : authorities) {
            roles.add(authority.getAuthority());
        }*/
        // 根据用户名，角色创建token并返回json信息
        String token = jwtTokenUtil.generateToken(user.getUsername(), authorities);
//        String token = JwtTokenUtils.createToken(user.getUsername(), roles, false);
//        response.setHeader("Bearer Token", token);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        Map<String, Object> authInfo = new HashMap<>(10) {{
            //        roles,
            //        userId,
            //        username: _username,
            //        token,
            //        realName,
            //        desc,
/*            userId: '1',
                    desc: 'manager',
                    password: '123456',
                    token: 'fakeToken1',
                    homePath: '/dashboard/analysis',
                    roles: [
            {
                roleName: 'Super Admin',
                        value: 'super',
            },
      ],*/
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
            HashMap<String, String> map = new HashMap<>() {{
                put("roleName", "Super Admin");
                put("value", "super");
            }};
            arrayList.add(map);
            put("roles", arrayList);
            put("userId", "1");
            put("username", user.getUsername());
            put("realName", user.getUsername());
            put("desc", "manager");
            put("token", SecurityKimParams.AUTH_KIM_PREFIX + token);
        }};

        writer.write(new ObjectMapper().writeValueAsString(ResultVO.success(authInfo)));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "登录失败，账号或密码错误");
    }


}
