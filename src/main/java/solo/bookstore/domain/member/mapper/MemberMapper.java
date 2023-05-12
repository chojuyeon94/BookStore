package solo.bookstore.domain.member.mapper;


import org.mapstruct.Mapper;
import solo.bookstore.domain.member.entity.Member;
import solo.bookstore.domain.member.dto.MemberDto;

@Mapper
public interface MemberMapper {

    default Member memberPostDtoToMember(MemberDto.Post post) {

        Member member = new Member();
        member.setEmail(post.getEmail());
        member.setPassword(post.getPassword());
        member.setNickname(post.getNickname());

        return member;
    }

    default Member memberPatchDtoToMember(MemberDto.Patch patch) {
        Member member = new Member();

        member.setPassword(patch.getPassword());
        member.setNickname(patch.getNickname());
        return member;
    }



}
