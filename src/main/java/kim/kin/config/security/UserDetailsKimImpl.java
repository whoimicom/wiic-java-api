package kim.kin.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


public class UserDetailsKimImpl extends User {
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

    public UserDetailsKimImpl(String username, String password) {
        super(username, password, AuthorityUtils.NO_AUTHORITIES);
    }

    public UserDetailsKimImpl(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

/*    public UserDetailsKimImpl(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }*/

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

    /**
     * @return getLoginType
     */
    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
