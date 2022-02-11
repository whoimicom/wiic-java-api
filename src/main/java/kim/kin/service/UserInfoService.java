package kim.kin.service;

import kim.kin.model.UserInfoDTO;
import kim.kin.model.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @author choky
 */
public interface UserInfoService {
    UserInfo save(UserInfoDTO dto);

    /**
     * @return showReplicaStatus
     */
    List<Map<String, Object>> showReplicaStatus();
}
