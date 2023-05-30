package solo.bookstore.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.bookstore.auth.jwt.JwtProvider;
import solo.bookstore.auth.repository.RefreshTokenRepository;
import solo.bookstore.domain.member.entity.Member;
import solo.bookstore.global.exception.BusinessLogicException;
import solo.bookstore.domain.member.repository.MemberRepository;
import solo.bookstore.global.exception.ExceptionCode;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private MemberRepository memberRepository;
    private JwtProvider jwtProvider;
    private BCryptPasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository, JwtProvider jwtProvider, BCryptPasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository){

        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
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
/*
    public Member updateMemberInfo(String token) {
        Member findMember = findByEmail(token);

        if (findMember.getPassword() != null) {
            findMember.setPassword(passwordEncoder.encode(findMember.getPassword()));
        }

        Optional.ofNullable(findMember.getNickname()).ifPresent(username -> findMember.setNickname(username));
        verifyExistsNickName(findMember.getNickname());

        return memberRepository.save(findMember);
    }
*/
public Member findByEmail() {
    String email = getCurrentMemberEmail();
    Optional<Member> optionalMember = memberRepository.findByEmail(email);
    return optionalMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.TOKEN_NOT_VALID));
}

    public String getCurrentMemberEmail() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public Member findByEmail(String email){
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
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

    public void deleteMember(HttpServletRequest request) {
        String accessToken = getAccessToken(request);

        Member findMember = findByEmail();

        memberRepository.delete(findMember);

        refreshTokenRepository.deleteByKey(findMember.getEmail());
        refreshTokenRepository.blacklistToken(accessToken);
    }

    @Transactional
    public void logout (HttpServletRequest request) {
        String accessToken = getAccessToken(request);
        String email = getCurrentMemberEmail();
        // 로그아웃 요청이 들어오면 기존 토큰 블랙리스트 처리
        refreshTokenRepository.blacklistToken(accessToken);
        // refresh 토큰도 제거
        refreshTokenRepository.deleteByKey(email);
    }

    private String getAccessToken(HttpServletRequest request) {
        return request.getHeader("Authorization").substring(7);
    }


}
