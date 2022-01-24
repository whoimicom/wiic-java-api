package kim.kin.config.security.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import kim.kin.config.security.JwtTokenUtil;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 处理身份验证表单提交。
 *
 * @author crush
 */
public class UsernamePasswordKimFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;

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
//            UserInfo userInfo = new ObjectMapper().readValue(request.getInputStream(), UserInfo.class);
//            String username = userInfo.getUsername();
//            String password = userInfo.getPassword();

            String username = this.obtainUsername(request);
            username = username != null ? username : "";
            username = username.trim();
            String password = this.obtainPassword(request);
            password = password != null ? password : "";
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
            return authenticationManager.authenticate(authentication);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthenticationServiceException(e.getMessage());
        }
    }

    /**
     * 成功验证后调用的方法
     * 如果验证成功，就生成token并返回
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        // 查看源代码会发现调用getPrincipal()方法会返回一个实现了`UserDetails`接口的对象
        // 所以就是JwtUser啦
        Object principal = authResult.getPrincipal();
        User user = (User) principal;

        List<String> roles = new ArrayList<>();

        // 因为在JwtUser中存了权限信息，可以直接获取，由于只有一个角色就这么干了
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            roles.add(authority.getAuthority());
        }

        // 根据用户名，角色创建token并返回json信息
        String token = jwtTokenUtil.generateToken(user);
//        String token = JwtTokenUtils.createToken(user.getUsername(), roles, false);
        response.setHeader("Bearer Token", token);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper().writeValueAsString("Bearer "+token));
    }

    /**
     * 验证失败时候调用的方法
     *
     * @param request
     * @param response
     * @param failed
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "登录失败，账号或密码错误");
    }
}
