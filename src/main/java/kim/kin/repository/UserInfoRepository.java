package kim.kin.repository;

import kim.kin.model.ResultKimListMap;
import kim.kin.model.UserInfo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * @author kin.kim
 * @since 2023-10-12
 **/
@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return findByUsername
     */
    Optional<UserInfo> findByUsername(String username);

    /**
     * @param email email
     * @return findByEmail
     */
    Optional<UserInfo> findByEmail(String email);

    /**
     * @return show replica status
     */
    @Query(value = "show replica status",resultSetExtractorClass = ResultKimListMap.class )
    List<Map<String, Object>> showReplicaStatus();


}
