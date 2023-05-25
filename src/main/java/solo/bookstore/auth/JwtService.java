package solo.bookstore.auth;

import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import solo.bookstore.global.exception.BusinessLogicException;
import solo.bookstore.global.exception.ExceptionCode;

import java.security.Key;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;

@Service
public class JwtService {

    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private Set<String> blacklistedToken = new HashSet<>();

    public String[] loginUser(String email, String password){

        String jwt = Jwts.builder().setSubject(email).signWith(key).compact();
        String refreshToken = Jwts.builder().setSubject(email).signWith(key).compact();

        return new String[]{jwt, refreshToken};

    }

    public void logoutUser(String accessToken, String refreshToken){

        blacklistedToken.add(accessToken);
        blacklistedToken.add(refreshToken);
    }

    public void validateToken(String token){

        if(blacklistedToken.contains(token)){
            throw new BusinessLogicException(ExceptionCode.TOKEN_NOT_VALID);
        }

        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

    }

}
