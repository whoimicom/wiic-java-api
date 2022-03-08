package kim.kin.utils;

import kim.kin.exception.ReqKimException;
import kim.kin.model.UserKimDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityKimUtils {
    private static final Logger log = LoggerFactory.getLogger(SecurityKimUtils.class);

    public static UserKimDetails getCurrentUser() {
//        try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserKimDetails) authentication.getPrincipal();
    /*    } catch (Exception e) {
           log.error("获取当前用户异常",e);
            throw new ReqKimException(HttpStatus.UNAUTHORIZED, "登录状态过期");
        }*/

    }

    public static String getUsername() {
        return getCurrentUser().getUsername();
    }
}
