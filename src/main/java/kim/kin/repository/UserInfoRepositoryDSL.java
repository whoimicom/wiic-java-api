package kim.kin.repository;

import com.infobip.spring.data.jdbc.QuerydslJdbcFragment;
import kim.kin.model.UserInfo;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


/**
 * @author choky
 */
@Repository
//public interface UserInfoRepositoryDSL  extends PagingAndSortingRepository<UserInfo, Integer>, QuerydslPredicateExecutor<UserInfo>, QuerydslJdbcFragment<UserInfo> {
public interface UserInfoRepositoryDSL  extends  PagingAndSortingRepository<UserInfo, Long>,QuerydslPredicateExecutor<UserInfo>, QuerydslJdbcFragment<UserInfo> {



}
