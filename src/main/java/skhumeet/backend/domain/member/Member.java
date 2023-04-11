package skhumeet.backend.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private int id;

    // 학번
    @Column(name = "member_number", nullable = false, unique = true)
    private String memberNumber;

    // 아이디
    @Column(nullable = false, unique = true)
    private String loginId;

    // 이름
    @Column(nullable = false)
    private String name;

    // 별명
    @Column(nullable = false)
    private String nickname;

    // 권한
    @Enumerated(EnumType.STRING)
    private Authority authority;

    // 프로필 이미지
    @Column(name = "profile_image")
    private String profileImage;

    public Member update(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }
}

