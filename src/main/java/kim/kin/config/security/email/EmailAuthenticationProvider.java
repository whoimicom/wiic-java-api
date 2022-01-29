package kim.kin.config.security.email;

import kim.kin.model.UserInfo;
import kim.kin.model.UserKimDetails;
import kim.kin.repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class EmailAuthenticationProvider implements AuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(EmailAuthenticationProvider.class);
    private final UserInfoRepository userInfoRepository;

    public EmailAuthenticationProvider(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }


    /**
     * 认证
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        log.info("EmailAuthenticationProvider  request: %s{}", authentication);
//        EmailAuthenticationToken token = (EmailAuthenticationToken) authentication;
        String email = authentication.getPrincipal().toString();
        String eCode = authentication.getCredentials().toString();
        Object details = authentication.getDetails();
        log.info("email:{},eCode:{}", email, eCode);
        if (!validateCode(email, eCode)) {
            throw new BadCredentialsException(email);
        }
        UserInfo userInfo = userInfoRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        if (userInfo == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }
        //通过用户获取权限
        List<String> permissionCodess = new ArrayList<>(1);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String permissionCode : permissionCodess) {
            if (permissionCode != null && permissionCode != "") {
                GrantedAuthority grantedAuthority =
                        new SimpleGrantedAuthority(permissionCode);
                authorities.add(grantedAuthority);
            }
        }

        String permissions = "/test,/auth,/index.html";
        UserKimDetails userDetails = new UserKimDetails(
                userInfo.getUsername(), userInfo.getPassword(), true, true, true, true,
                AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
        userDetails.setTheme("");
        userDetails.setAvatar(userInfo.getAvatar());
        userDetails.setEmail(userInfo.getEmail());
        userDetails.setMobile(userInfo.getMobile());
        userDetails.setGender(userInfo.getGender());
        userDetails.setId(userInfo.getId());
        userDetails.setPassword(userInfo.getPassword());
        userDetails.setLoginTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        EmailAuthenticationToken result = new EmailAuthenticationToken(userDetails, eCode, authorities);
//        EmailAuthenticationToken result = new EmailAuthenticationToken(user, user.getAuthorities());
                /*
                Details 中包含了 ip地址、 sessionId 等等属性 也可以存储一些自己想要放进去的内容
                */
        result.setDetails(details);
        return result;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return EmailAuthenticationToken.class.isAssignableFrom(aClass);
    }

    /**
     * validateCode
     *
     * @param email email
     * @param ecode ecode
     * @return validateResult
     */
    private boolean validateCode(String email, String ecode) {
        //TODO
        return email.equals(ecode);
    }
}
