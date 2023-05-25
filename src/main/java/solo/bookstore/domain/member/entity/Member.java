package solo.bookstore.domain.member.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import solo.bookstore.global.audit.BaseTime;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE member SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Member extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 8, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private BigDecimal mileage;

    private boolean deleted = false;

    private boolean isAdmin = false;

    public Member(String email, String password, String nickname, BigDecimal mileage){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.mileage = new BigDecimal(0);
    }

}
