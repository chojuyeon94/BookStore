package solo.bookstore.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solo.bookstore.domain.member.dto.MemberDto;
import solo.bookstore.domain.member.entity.Member;
import solo.bookstore.domain.member.mapper.MemberMapper;
import solo.bookstore.domain.member.service.MemberService;
import solo.bookstore.global.response.SingleResponseDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/members")
@CrossOrigin
public class MemberController {

    private final MemberService memberService;

    private final MemberMapper memberMapper;

    public MemberController(MemberService memberService, MemberMapper memberMapper) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
    }


    ResponseEntity<MemberDto.Post> postMember(@Valid @RequestBody MemberDto.Post postDto){

        Member member =memberService.createMember(memberMapper.memberPostDtoToMember(postDto));
        //return new ResponseEntity(new SingleResponseDto<>(memberMapper.createMemberToMemberResponseDto(member)), HttpStatus.CREATED);

    }

}
