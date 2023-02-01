package kim.kin.rest;

import kim.kin.config.security.AnonymousKimAccess;
import kim.kin.config.security.JwtTokenUtil;
import kim.kin.config.security.SecurityKimParams;
import kim.kin.config.security.user.UserDetailsServiceImpl;
import kim.kin.kklog.LogKimAnnotation;
import kim.kin.model.*;
import kim.kin.repository.UserInfoJdbcTemplate;
import kim.kin.service.UserInfoService;
import kim.kin.utils.SecurityKimUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author choky
 */
@RestController
@CrossOrigin
public class AuthenticateRest {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserInfoService userInfoService;
//    private final UserInfoRepositoryDSL userInfoRepositoryDSL;
    private final JdbcTemplate jdbcTemplate;
    private final UserInfoJdbcTemplate userInfoJdbcTemplate;

    public AuthenticateRest(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserDetailsServiceImpl userDetailsService, UserInfoService userInfoService, JdbcTemplate jdbcTemplate, UserInfoJdbcTemplate userInfoJdbcTemplate) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userInfoService = userInfoService;
        this.jdbcTemplate = jdbcTemplate;
        this.userInfoJdbcTemplate = userInfoJdbcTemplate;
    }

    @RequestMapping(value = "/login1", method = RequestMethod.POST)
    @LogKimAnnotation
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserInfoDTO userInfoDTO) {
        String username = userInfoDTO.getUsername();
        String password = userInfoDTO.getPassword();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserKimDetails user = userDetailsService.loadUserByUsername(username);
        String token = jwtTokenUtil.generateToken(username, user.getAuthorities());
        Map<String, Object> authInfo = new HashMap<>(1) {{
            put("token", SecurityKimParams.AUTH_KIM_PREFIX + token);
        }};
        return ResponseEntity.ok(authInfo);
    }

    //    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    @GetMapping("/getUserInfo")
    public ResponseEntity<?> getUserInfo() {
        UserKimDetails currentUser = SecurityKimUtils.getCurrentUser();
        return ResponseEntity.ok(ResultVO.success(currentUser));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @LogKimAnnotation
    public ResponseEntity<?> saveUser(@RequestBody UserInfoDTO user) {
        return ResponseEntity.ok(userInfoService.save(user));
    }

    @RequestMapping(value = "/currentUser", method = RequestMethod.POST)
    @LogKimAnnotation
    public ResponseEntity<?> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Object principal = authentication.getPrincipal();
        Object credentials = authentication.getCredentials();
        Object details = authentication.getDetails();
        System.out.println(authorities);
        System.out.println(principal);
        System.out.println(credentials);
        System.out.println(details);
        return ResponseEntity.ok(authentication);
    }

//    @RequestMapping(value = "/showReplicaStatus", method = RequestMethod.POST)
//    @AnonymousKimAccess
//    public ResponseEntity<?> showReplicaStatus() {
//        List<Map<String, Object>> maps = userInfoService.showReplicaStatus();
//        return ResponseEntity.ok(maps);
//    }

//    @GetMapping("/page")
//    @AnonymousKimAccess
//    public ResponseEntity<?> page(@QuerydslPredicate(root = UserInfo.class) Predicate predicate, final Pageable pageable) {
//        if (predicate == null) predicate = new BooleanBuilder();
//        Page<UserInfo> dsl = userInfoRepositoryDSL.findAll(predicate, pageable);
//        return ResponseEntity.ok(dsl);
//    }
    @GetMapping("/temp")
    @AnonymousKimAccess
    public ResponseEntity<?> temp() {
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select * from kk_user_info");
        mapList.forEach(System.out::println);
        return ResponseEntity.ok(mapList);
    }
    @PostMapping("/tempPage")
    @AnonymousKimAccess
    public ResponseEntity<?> tempPage(Pageable page) {
        Page<UserInfo> all = userInfoJdbcTemplate.findAll(page);
        return ResponseEntity.ok(all);
    }

//    @GetMapping("/dsl")
//    @AnonymousKimAccess
//    public ResponseEntity<?> dsl() {
////        if (predicate == null) predicate = new BooleanBuilder();
//        Optional<UserInfo> userInfo = userInfoRepositoryDSL.queryOne(query -> query
//                .select(userInfoRepositoryDSL.entityProjection())
//                .from(QUserInfo.userInfo)
//                .where(QUserInfo.userInfo.username.in("kinkim", "admin"))
//                .orderBy(QUserInfo.userInfo.realName.asc(), QUserInfo.userInfo.id.asc())
//                .limit(1)
//                .offset(1));
////        Page<UserInfo> dsl = userInfoService.dsl(predicate, pageable);
//        return ResponseEntity.ok(userInfo);
//    }

    @PostMapping(value = "/user/logout")
    @LogKimAnnotation
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("SUCCESS");
    }

    @PostMapping(value = "/getInfo")
    @LogKimAnnotation
    public ResponseEntity<?> getInfo() {
        UserInfoDTO us = new UserInfoDTO();
        us.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        us.setEnabled(true);
        us.setIntroduction("I am a super administrator");
        us.setUsername("admin");
        us.setPassword("pwd");
        us.setRoles("admin");
//        path: 'page',
//                component: 'views/permission/page',
//                name: 'PagePermission',
//                meta: {
//                  title: 'Page Permission',
//                    roles: ['admin']
//        }

        MetaVO metaVO1 = new MetaVO("Page Permission", "lock");
        MetaVO metaVO = new MetaVO("PermissionTitle", "lock");
        UserPermissionVO userPermissionVO1 = new UserPermissionVO("PagePermission", "page", "", "permission/page", false, metaVO1, null);
        UserPermissionVO userPermissionVO2 = new UserPermissionVO("RolePermission", "role", "", "permission/role", false, metaVO1, null);
        List<UserPermissionVO> userPermissionVOS = Arrays.asList(userPermissionVO1, userPermissionVO2);
//        UserPermissionVO userPermissionVO = new UserPermissionVO("permission", "/permission", "/permission/page", "Layout", false, metaVO, Collections.singletonList(userPermissionVO1));
        UserPermissionVO userPermissionVO = new UserPermissionVO("permissionName", "/permission", "", "Layout", true, metaVO, userPermissionVOS);
        us.setVo(Collections.singletonList(userPermissionVO));

//        return ResponseEntity.ok(jwtTokenUtil.getCurrentUser());
        return ResponseEntity.ok(us);
    }

}
