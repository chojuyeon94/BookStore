package solo.bookstore.auth.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import solo.bookstore.auth.repository.RefreshTokenRepository;
import solo.bookstore.auth.utils.CustomAuthUtils;
import solo.bookstore.domain.member.repository.MemberRepository;
import solo.bookstore.global.exception.SecurityAuthException;
import solo.bookstore.global.exception.SecurityAuthExceptionCode;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final CustomAuthUtils customAuthUtils;

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException{

        try {
            Claims accessToken = verifyJws(httpServletRequest, httpServletResponse);
            if(accessToken != null) setAuthenticationToContext(accessToken);
        }

        catch(SignatureException signatureException){
            httpServletRequest.setAttribute("exception", signatureException);
        }

        catch (SecurityAuthException securityAuthException){
            httpServletRequest.setAttribute("exception", securityAuthException);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest httpServletRequest) throws ServletException {
        String authorization = httpServletRequest.getHeader("Authorization");
        return authorization == null || !authorization.startsWith("Bearer");
    }

    private Claims verifyJws(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String accessToken = httpServletRequest.getHeader("Authorization").substring(7);
        String refreshToken = httpServletRequest.getHeader("Refresh");

        if (refreshTokenRepository.findByKey(accessToken) != null) {
            throw new SecurityAuthException(SecurityAuthExceptionCode.MEMBER_LOGOUT);
        }

        return jwtProvider.parseClaims(accessToken);
    }

    private void setAuthenticationToContext(Claims claims) {
        String email = (String) claims.get("email");
        List<GrantedAuthority> authorities = customAuthUtils.createAuthorities((List)claims.get("roles"));

        UsernamePasswordAuthenticationToken token =
                UsernamePasswordAuthenticationToken.authenticated(email, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(token);
    }

}
