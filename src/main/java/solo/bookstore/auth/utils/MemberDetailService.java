package solo.bookstore.auth.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import solo.bookstore.domain.member.entity.Member;
import solo.bookstore.domain.member.service.MemberService;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    private final MemberService memberService;

    private final CustomAuthUtils customAuthUtils;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member findedMember = memberService.findByEmail(email);
        return new MemberDetails(findedMember);
    }

    @Getter
    @AllArgsConstructor
    public class MemberDetails implements UserDetails {
        private Member member;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return customAuthUtils.createAuthorities(member.getRoles());
        }

        @Override
        public String getUsername() {
            return member.getEmail();
        }

        @Override
        public String getPassword() {
            return member.getPassword();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

    }

}
