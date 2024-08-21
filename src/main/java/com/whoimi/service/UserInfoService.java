package com.whoimi.service;

import com.whoimi.model.UserInfoDTO;
import com.whoimi.model.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @author whoimi
 * @since 2023-10-12
 **/
public interface UserInfoService {
    UserInfo save(UserInfoDTO dto);

    /**
     * @return showReplicaStatus
     */
    List<Map<String, Object>> showReplicaStatus();
}
