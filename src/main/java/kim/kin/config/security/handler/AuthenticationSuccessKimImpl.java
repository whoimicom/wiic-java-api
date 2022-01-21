package kim.kin.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kim.kin.config.security.JwtTokenUtil;
import kim.kin.model.UserKimDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 处理邮件验证登录成功处理
 * @Author: crush
 * @Date: 2021-09-09 9:21
 * version 1.0
 */
@Component
public class AuthenticationSuccessKimImpl implements AuthenticationSuccessHandler {
    @Autowired
    private  JwtTokenUtil jwtTokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 查看源代码会发现调用getPrincipal()方法会返回一个实现了`UserDetails`接口的对象
        // 所以就是JwtUser啦
        UserKimDetails user = (UserKimDetails) authentication.getPrincipal();

        List<String> roles=new ArrayList<>();

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            roles.add( authority.getAuthority());
        }

        // 根据用户名，角色创建token并返回json信息
        String token = jwtTokenUtil.generateToken(user);
//        String token = JwtTokenUtils.createToken(user.getUsername(), roles, false);

        user.setPassword(null);

        response.setHeader("Bearer Token",token);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper().writeValueAsString("success"));
    }
}
