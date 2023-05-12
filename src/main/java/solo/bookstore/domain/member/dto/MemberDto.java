package solo.bookstore.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class MemberDto {

    @AllArgsConstructor
    @Getter
    public static class Post {
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+.(com|net)$", message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank
        @Length(min = 6, max = 20, message = "비밀번호는 최소 4자 이상이 되어야 합니다.")
        private String password;

        @NotBlank(message = "닉네임을 입력 해주세요")
        @Length(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력 해주세요.")
        private String nickname;
    }

    @Getter
    @Setter
    public static class Patch {

        private String password;
        private String nickname;

    }

    @AllArgsConstructor
    @Getter
    public static class Response {
        private long memberId;
        private String email;
        private String nickname;
        private LocalDateTime createDate;

    }

    @AllArgsConstructor
    @Getter
    public static class CreateResponse {
        private long memberId;
        private String email;
        private String nickname;
        private LocalDateTime createDate;
    }

}
