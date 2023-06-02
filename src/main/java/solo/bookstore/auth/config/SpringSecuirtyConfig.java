package solo.bookstore.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.CrossOrigin;
import solo.bookstore.auth.jwt.JwtProvider;
import solo.bookstore.auth.repository.RefreshTokenRepository;
import solo.bookstore.auth.utils.CustomAuthUtils;
import solo.bookstore.domain.member.repository.MemberRepository;
import solo.bookstore.domain.member.service.MemberService;

@Configuration
@CrossOrigin
public class SpringSecuirtyConfig {

    private final JwtProvider jwtProvider;

    private final CustomAuthUtils customAuthUtils;

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberRepository memberRepository;

    private final MemberService memberService;


    public SpringSecuirtyConfig(JwtProvider jwtProvider, CustomAuthUtils customAuthUtils, RefreshTokenRepository refreshTokenRepository, MemberRepository memberRepository, MemberService memberService){
        this.jwtProvider = jwtProvider;
        this.customAuthUtils = customAuthUtils;
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint()

        return http.build();
    }


}
