package solo.bookstore.auth;

import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import solo.bookstore.global.exception.BusinessLogicException;
import solo.bookstore.global.exception.ExceptionCode;
import org.springframework.data.redis.core.RedisTemplate;

import java.security.Key;
import java.util.Date;


@Service
public class JwtService {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final RedisTemplate<String, Boolean> redisTemplate;

    public JwtService(RedisTemplate<String, Boolean> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String[] loginUser(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (1000 * 60 * 60));  // token valid for 1 hour
        Date refreshExpiryDate = new Date(now.getTime() + (1000 * 60 * 60 * 24));  // refresh token valid for 24 hours

        String jwt = Jwts.builder().setSubject(email).setIssuedAt(now).setExpiration(expiryDate).signWith(key).compact();
        String refreshToken = Jwts.builder().setSubject(email).setIssuedAt(now).setExpiration(refreshExpiryDate).signWith(key).compact();

        return new String[]{jwt, refreshToken};
    }

    public void logoutUser(String accessToken, String refreshToken) {
        redisTemplate.opsForValue().set(accessToken, true);
        redisTemplate.opsForValue().set(refreshToken, true);
    }

    public void validateToken(String token) {
        if(redisTemplate.opsForValue().get(token) != null){
            throw new BusinessLogicException(ExceptionCode.TOKEN_NOT_VALID);
        }

        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String refreshToken(String oldToken, String refreshToken){
        validateToken(refreshToken);
        return loginUser(getEmailFromToken(oldToken))[0];
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}