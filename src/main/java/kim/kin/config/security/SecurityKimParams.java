package kim.kin.config.security;

/**
 * @author choky
 */
public class SecurityKimParams {
    /**
     * login uri
     */
    public static final String LOGIN_URI = "/authenticate";
    /**
     * register uri
     */
    public static final String REGISTER_URI = "/register";

    /**
     * request.getHeader("Authorization")
     */
    public static final String AUTH_KIM_HEADER = "Authorization";
    /**
     * Bearer token
     */
    public static final String AUTH_KIM_PREFIX = "Bearer ";
}
