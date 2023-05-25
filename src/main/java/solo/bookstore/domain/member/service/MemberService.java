package solo.bookstore.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.bookstore.auth.JwtService;
import solo.bookstore.domain.member.entity.Member;
import solo.bookstore.global.exception.BusinessLogicException;
import solo.bookstore.domain.member.repository.MemberRepository;
import solo.bookstore.global.exception.ExceptionCode;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private MemberRepository memberRepository;
    private JwtService jwtService;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, JwtService jwtService, BCryptPasswordEncoder passwordEncoder){

        this.memberRepository = memberRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;

    }

    public Member postMember(Member member){

        Optional<Member> checkExistMember = memberRepository.findByEmail(member.getEmail());
        if(checkExistMember.isPresent()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }

        String hashedPassword = passwordEncoder.encode(member.getPassword());

        Member newMember = new Member();
        newMember.setEmail(member.getEmail());
        newMember.setPassword(hashedPassword);
        newMember.setNickname(member.getNickname());

        return memberRepository.save(newMember);

    }



}
