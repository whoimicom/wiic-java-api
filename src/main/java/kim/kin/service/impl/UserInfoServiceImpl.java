package kim.kin.service.impl;

import kim.kin.model.UserInfo;
import kim.kin.model.UserInfoDTO;
import kim.kin.repository.UserInfoRepository;
import kim.kin.service.UserInfoService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author kin.kim
 * @since 2023-10-12
 **/
@Service
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoRepository userInfoRepository;


    public UserInfoServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public UserInfo save(UserInfoDTO dto) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(dto.getUsername());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        userInfo.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        return userInfoRepository.save(userInfo);
    }

    @Override
    public List<Map<String, Object>> showReplicaStatus() {
        return userInfoRepository.showReplicaStatus();
    }
}
