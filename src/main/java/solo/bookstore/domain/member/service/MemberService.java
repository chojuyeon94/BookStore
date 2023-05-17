package solo.bookstore.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import solo.bookstore.domain.member.entity.Member;
import solo.bookstore.global.exception.BusinessLogicException;
import solo.bookstore.domain.member.repository.MemberRepository;
import solo.bookstore.global.exception.ExceptionCode;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    //private final CustomAuthorityUtils authorityUtils;

    public Member createMember(Member member){

        verifyExistsEmail(member.getEmail());
        verifyExistsNickName(member.getNickname());

        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);

        //List<String> roles = authorityUtils.createRoles(member.getEmail());
        //member.setRoles(roles);


        Member savedMember = memberRepository.save(member);

        return savedMember;

    }

    private void verifyExistsEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent())
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
    }

    private void verifyExistsNickName(String nickname) {
        Optional<Member> member = memberRepository.findByNickname(nickname);
        if (member.isPresent())
            throw new BusinessLogicException(ExceptionCode.MEMBER_NICKNAME_EXISTS);
    }
}
