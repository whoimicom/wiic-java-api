package kim.kin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if (s == null || "".equals(s))
        {
            throw new RuntimeException("用户不能为空");
        }
        // 调用方法查询用户
        Users user = usersRepository.findByUsername(s);
        if (user == null)
        {
            throw new RuntimeException("用户不存在");
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        for (Role role:usersRepository.findByUsername(s))
//        {
//            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
//        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(),"{noop}"+user.getPassword(),authorities);
    }
}