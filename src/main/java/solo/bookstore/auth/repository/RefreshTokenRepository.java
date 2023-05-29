package solo.bookstore.auth.repository;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import solo.bookstore.auth.jwt.JwtProvider;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepository {

    private final JwtProvider jwtProvider;

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String email, String refreshToken){

        redisTemplate.opsForValue().set(email, refreshToken,
                jwtProvider.parseClaims(refreshToken).getExpiration().getTime() - new Date().getTime(), TimeUnit.MINUTES);

    }


    public String findByKey(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteByKey(String key){
        redisTemplate.delete(key);
    }

    public void blacklistToken(String accessToken){

        Claims claims = jwtProvider.parseClaims(accessToken);
        Date expiration = claims.getExpiration();

        redisTemplate.opsForValue().set(accessToken, "LogOut", expiration.getTime() - new Date().getTime(), TimeUnit.MINUTES);
    }


}
