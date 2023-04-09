package skhumeet.backend.domain.study;

import lombok.*;
import skhumeet.backend.domain.member.BaseTime;
import skhumeet.backend.domain.member.Member;

import javax.persistence.*;

//post로 스크랩 추가, delete로 스크랩 삭제

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikePost extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //저장한 member id
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    //저장한 post id
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

}
