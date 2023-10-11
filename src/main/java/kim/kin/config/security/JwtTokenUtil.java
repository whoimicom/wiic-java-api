package kim.kin.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

/**
 * @author choky
 */
@Component
public class JwtTokenUtil implements Serializable, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    public static final String AUTH_BEARER = "Bearer ";
    public static final String AUTH_USER_ID = "AUTH_USER_ID";
    public static final String AUTH_USERNAME = "AUTH_USERNAME";
    public static final String AUTH_AUTHORITIES = "AUTH_AUTHORITIES";
    private Key key;

//    public static SecretKey generalKeyByDecoders() {
//        return Keys.hmacShaKeyFor(Decoders.BASE64.decode("cuAihCz53DZRjZwbsGcZJ2Ai6At+T142uphtJMsk7iQ="));
//
//    }

    /**
     * jwt base64EncodedSecretKey
     */
    @Value("${jwt.base64EncodedSecretKey}")
    private String base64EncodedSecretKey;

    /**
     * jwt expiration time (s)
     */
    @Value("${jwt.expiration}")
    private Long expiration;


    /**
     * while creating the token -
     * 1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
     * 2. Sign the JWT using the HS512 algorithm and secret key.
     * 3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
     * compaction of the JWT to a URL-safe string
     *
     * @param username    username
     * @param authorities authorities
     * @return token
     */
    public String generateToken(String id, String username, Collection<? extends GrantedAuthority> authorities) {
        Claims claims = new DefaultClaims();
        Set<String> authorityListToSet = AuthorityUtils.authorityListToSet(authorities);
//        claims.put(AUTH_AUTHORITIES, genAuthorities(authorities));
        claims.put(AUTH_AUTHORITIES, authorityListToSet);
        return Jwts.builder()
                .setClaims(claims)
                .setId(id)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
//                .signWith(key)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * retrieve username from jwt token
     *
     * @param token token
     * @return getUsernameFromToken
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token, Claims::getSubject);
    }

    /**
     * @param token token
     * @return userid
     */
    public String getIdFromToken(String token) {
        return getClaimsFromToken(token, Claims::getId);
    }

    /**
     * retrieve expiration date from jwt token
     *
     * @param token token
     * @return getExpirationDateFromToken
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimsFromToken(String token, Function<Claims, T> claimsResolver) {
//        Jws<Claims> claimsJws1 = Jwts.parser().setSigningKey(base64EncodedSecretKey).parseClaimsJws(token);
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//        Object scope = claimsJws.getBody().get("scope");
        Object authorities = claimsJws.getBody().get(AUTH_AUTHORITIES);
        Claims claims = claimsJws.getBody();

        String id = claims.getId();
        String subject = claims.getSubject();
        String issuer = claims.getIssuer();
        Date expiration = claims.getExpiration();
        Date notBefore = claims.getNotBefore();
        String audience = claims.getAudience();
        Date issuedAt = claims.getIssuedAt();
        log.debug(" authorities:{},id:{} Subject:{} Issuer:{} Expiration:{} getNotBefore:{} getAudience:{} getIssuedAt:{} "
                , authorities, id, subject, issuer, expiration, notBefore, audience, issuedAt);
        return claimsResolver.apply(claims);
    }

    public Claims getClaimsFromToken(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return claimsJws.getBody();

    }

    /**
     * getAuthentication
     *
     * @param token token
     * @return List<GrantedAuthority>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<GrantedAuthority> getAuthentication(String token) {
        List<GrantedAuthority> authorities = new ArrayList<>(10);
        ArrayList value = (ArrayList) getTokenBody(token).get(AUTH_AUTHORITIES);
        value.forEach(rule -> authorities.add(new SimpleGrantedAuthority(rule.toString())));
        return authorities;
    }

    private Claims getTokenBody(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                .getBody();
    }

    /**
     * check if the token has expired
     *
     * @param token token
     * @return isTokenExpired
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    /**
     * validate token
     *
     * @param token    token
     * @param username username
     * @return result
     */
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    public List<GrantedAuthority> genAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "userAuthorities cannot be null");
        List<GrantedAuthority> list = new ArrayList<>(authorities.size());
        for (GrantedAuthority authority : authorities) {
            list.add(new SimpleGrantedAuthority(authority.getAuthority()));
        }
        return list;
    }


    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
}
