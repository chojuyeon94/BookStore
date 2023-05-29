package solo.bookstore.auth.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import solo.bookstore.domain.member.entity.Member;
import solo.bookstore.global.exception.BusinessLogicException;
import solo.bookstore.global.exception.ExceptionCode;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtProvider {
    private static final String CLAIM_KEY_ROLES = "roles";
    private static final String CLAIM_KEY_EMAIL = "email";
    private static final String CLAIM_KEY_NICKNAME = "nickname";

    private Key key;

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    public JwtProvider(@Value("${jwt.key}") String secretKey) {
        String base64EncodedSecretKey = Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Member member) {
        return generateToken(member, accessTokenExpirationMinutes);
    }

    public String generateRefreshToken(Member member) {
        return generateToken(member, refreshTokenExpirationMinutes);
    }

    private String generateToken(Member member, int expirationMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationMinutes);

        return Jwts.builder()
                .setClaims(getClaims(member))
                .setSubject(member.getEmail())
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(calendar.getTime())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Map<String, Object> getClaims(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_ROLES, member.getRoles());
        claims.put(CLAIM_KEY_EMAIL, member.getEmail());
        claims.put(CLAIM_KEY_NICKNAME, member.getNickname());
        return claims;
    }

    public Claims parseClaims(String jws) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jws)
                    .getBody();
        } catch (JwtException e) {
            throw new BusinessLogicException(ExceptionCode.TOKEN_NOT_VALID);
        }
    }
}