package kim.kin.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

/**
 * @author choky
 */
@Component
public class JwtTokenUtil implements Serializable {

    @Serial
    private static final long serialVersionUID = -2550185165626007488L;

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
     * retrieve username from jwt token
     *
     * @param token token
     * @return getUsernameFromToken
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * retrieve expiration date from jwt token
     *
     * @param token token
     * @return getExpirationDateFromToken
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(base64EncodedSecretKey).parseClaimsJws(token);
        Object scope = claimsJws.getBody().get("scope");
        Object authorities = claimsJws.getBody().get("authorities");
        System.out.println(scope);
        System.out.println(authorities);
        Claims claims = claimsJws.getBody();
        System.out.println("ID: " + claims.getId());
        System.out.println("Subject: " + claims.getSubject());
        System.out.println("Issuer: " + claims.getIssuer());
        System.out.println("Expiration: " + claims.getExpiration());
        System.out.println("getNotBefore: " + claims.getNotBefore());
        System.out.println("getAudience: " + claims.getAudience());
        System.out.println("getIssuedAt: " + claims.getIssuedAt());
        return claimsResolver.apply(claims);
    }

    /**
     * getAuthentication
     *
     * @param token token
     * @return List<GrantedAuthority>
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public List<GrantedAuthority> getAuthentication(String token) {
        List<GrantedAuthority> authorities = new ArrayList<>(10);
        ArrayList value = (ArrayList) getTokenBody(token).get("authorities");
        value.forEach(rule -> authorities.add(new SimpleGrantedAuthority(rule.toString())));
        return authorities;
    }

    private Claims getTokenBody(String token) {
        return Jwts.parser()
                .setSigningKey(base64EncodedSecretKey)
                .parseClaimsJws(token)
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
    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>(10);
//        String username = userDetails.getUsername();
        List<String> strings = Arrays.asList("/admin", "/index", "/index.html");
//        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        claims.put("scope", strings);
        claims.put("authorities", genAuthorities(authorities));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, base64EncodedSecretKey)
                .compact();
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


}
