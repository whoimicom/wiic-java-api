package kim.kin.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsersRepository extends JpaRepository<Users, Long>, JpaSpecificationExecutor<Users> {

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return /
     */
    Users findByUsername(String username);


}
