package kim.kin.config.security.user;

import kim.kin.model.UserInfo;
import kim.kin.repository.UserInfoRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author choky
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserInfoRepository userInfoRepository;

    public UserDetailsServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        //通过用户获取权限
        List<String> permission = new ArrayList<>(1);
        permission.add("admin");
        permission.add("/userInfo");
        permission.add("/currentUser");
        permission.add("/hello");
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String permissionCode : permission) {
            if (permissionCode != null && !permissionCode.equals("")) {
                GrantedAuthority grantedAuthority =
                        new SimpleGrantedAuthority(permissionCode);
                authorities.add(grantedAuthority);
            }
        }
//        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("admin"));
        return new User(userInfo.getUsername(), userInfo.getPassword(), authorities);
    }


}
