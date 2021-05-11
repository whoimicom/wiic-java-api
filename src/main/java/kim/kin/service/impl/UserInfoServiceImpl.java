package kim.kin.service.impl;

import kim.kin.model.UserInfoDTO;
import kim.kin.model.UserInfo;
import kim.kin.repository.UserInfoRepository;
import kim.kin.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author choky
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserInfo save(UserInfoDTO dto) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(dto.getUsername());
        userInfo.setPassword(bcryptEncoder.encode(dto.getPassword()));
        return userInfoRepository.save(userInfo);
    }
}
