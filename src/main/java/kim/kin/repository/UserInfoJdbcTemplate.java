package kim.kin.repository;

import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import kim.kin.model.UserInfo;


@Repository
public class UserInfoJdbcTemplate {
    private final JdbcTemplate jdbcTemplate;

    public UserInfoJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // sort by single column name
/*  public List<User> findAll(Sort sort) {
    
    Order order = sort.toList().get(0);
    
    return jdbcTemplate.query("SELECT * FROM USER ORDER BY "+order.getProperty()+" "+order.getDirection().name(),
         (rs, rowNum) -> mapUserResult(rs));
  }*/

    private UserInfo mapUserResult(final ResultSet rs) throws SQLException {

        UserInfo userInfo = new UserInfo();
        userInfo.setId(rs.getLong("id"));
        userInfo.setAvatar(rs.getString("avatar"));
        userInfo.setDesc(rs.getString("desc"));
        userInfo.setEmail(rs.getString("email"));
        userInfo.setEnabled(rs.getBoolean("enabled"));
        userInfo.setGender(rs.getString("gender"));
        userInfo.setHomePath(rs.getString("home_path"));
        userInfo.setIntroduction(rs.getString("introduction"));
        userInfo.setMobile(rs.getString("mobile"));
        userInfo.setRealName(rs.getString("real_name"));
        userInfo.setUsername(rs.getString("username"));
        return userInfo;
    }

    // defaults sorts by Id if order not provided
    public Page<UserInfo> findAll(Pageable page) {
        String sql = "select * from kk_user_info";
        List<UserInfo> userInfoList = jdbcTemplate.query(sql + " limit " + page.getPageSize() + " OFFSET " + page.getOffset(), (rs, rowNum) -> mapUserResult(rs));

//    Order order = !page.getSort().isEmpty() ? page.getSort().toList().get(0) : Order.by("ID");
//    List<User> users = jdbcTemplate.query("SELECT * FROM USER ORDER BY " + order.getProperty() + " "
//                    + order.getDirection().name() + " LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset(),
//            (rs, rowNum) -> mapUserResult(rs));
//    return new PageImpl<User>(users, page, count());
        return new PageImpl<>(userInfoList, page, userInfoList.size());
    }
}