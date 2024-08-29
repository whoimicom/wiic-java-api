package com.whoimi.rest;

import com.whoimi.config.security.AnonymousKimAccess;
import com.whoimi.model.SysLibrary;
import com.whoimi.model.UserInfo;
import com.whoimi.repository.mysql.UserInfoJdbcTemplate;
import com.whoimi.repository.mysql.UserInfoRepository;
import com.whoimi.repository.oracle.SysLibraryRepository;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author whoimi
 * @since 2024-08-29
 **/
@RestController
@RequestMapping("/dataSource")
public class DataSourceRest {
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource(name = "oracleJdbcTemplate")
    private JdbcTemplate oracleJdbcTemplate;
    @Resource
    private UserInfoJdbcTemplate userInfoJdbcTemplate;
    @Resource
    private SysLibraryRepository sysLibraryRepository;
    @Resource
    private UserInfoRepository userInfoRepository;

    @GetMapping("/temp")
    @AnonymousKimAccess
    public ResponseEntity<?> temp() {
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select * from kk_user_info limit 10");
        List<Map<String, Object>> maps = oracleJdbcTemplate.queryForList("SELECT * FROM SYS_LIBRARY WHERE ROWNUM < 10 ORDER BY SYS_LIBRARY.LIB_ID DESC");
        mapList.forEach(System.out::println);
        maps.forEach(System.out::println);
        return ResponseEntity.ok(mapList);
    }

    @PostMapping("/tempPage")
    @AnonymousKimAccess
    public ResponseEntity<?> tempPage(Pageable page) {
        Page<UserInfo> all = userInfoJdbcTemplate.findAll(page);
        return ResponseEntity.ok(all);
    }

    @PostMapping("/dsAll")
    @AnonymousKimAccess
    public ResponseEntity<?> sysLibrary() {
        Iterable<UserInfo> all = userInfoRepository.findAll();
        all.forEach(System.out::println);
        List<SysLibrary> top100 = sysLibraryRepository.findTop100OrderByLibIdDesc();
        return ResponseEntity.ok(top100);
    }
}
