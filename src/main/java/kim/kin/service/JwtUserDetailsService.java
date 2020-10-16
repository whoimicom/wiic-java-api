package kim.kin.service;

import kim.kin.model.User;
import kim.kin.model.UserDTO;
import kim.kin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;


    @Override
    public org.springframework.security.core.userdetails.User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("admin"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    public User save(UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(bcryptEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }
}
