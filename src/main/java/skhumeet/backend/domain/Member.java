package skhumeet.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private int id;

    // 학번
    @Column(name = "member_number", nullable = false, unique = true)
    private String memberNumber;

    // 아이디
    @Column(nullable = false, unique = true)
    private String email;

    // 비밀번호
    @Column(nullable = false)
    private String password;

    // 이름
    @Column(nullable = false)
    private String name;

    // 권한
    @Enumerated(EnumType.STRING)
    private Authority authority;
}
