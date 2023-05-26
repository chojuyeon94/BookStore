package solo.bookstore.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    public Member updateMemberInfo(String token) {
        Member findMember = findByEmail(token);

        if (findMember.getPassword() != null) {
            findMember.setPassword(passwordEncoder.encode(findMember.getPassword()));
        }

        Optional.ofNullable(findMember.getNickname()).ifPresent(username -> findMember.setNickname(username));
        verifyExistsNickName(findMember.getNickname());

        return memberRepository.save(findMember);
    }

    @Transactional(readOnly = true)
    public Member findMember (String token) {
        Member authMember = findByEmail(token);

        return authMember;
    }

    @Transactional(readOnly = true)
    public Member findVerifiedMember(long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member findMember = optionalMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMember;

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

    public Member findByEmail(String token) {
        String email = jwtService.getEmailFromToken(token);
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.TOKEN_NOT_VALID));
    }



}
