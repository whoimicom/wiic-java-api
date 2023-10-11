package kim.kin.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


public class UserDetailsImpl extends User {
    private String bearer;
    /**
     * web
     * wechat
     * app
     */
    String accessChannel;
    /**
     * USERNAME_PASSWORD
     * TELEPHONE_VER_CODE
     * EMAIL_VER_CODE
     */
    String loginType;

    public UserDetailsImpl(String username, String password) {
        super(username, password, AuthorityUtils.NO_AUTHORITIES);
    }

    public UserDetailsImpl(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public UserDetailsImpl(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public String getBearer() {
        return bearer;
    }

    public void setBearer(String bearer) {
        this.bearer = bearer;
    }

    public String getAccessChannel() {
        return accessChannel;
    }

    public void setAccessChannel(String accessChannel) {
        this.accessChannel = accessChannel;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
