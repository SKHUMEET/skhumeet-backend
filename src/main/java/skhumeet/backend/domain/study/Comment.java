package skhumeet.backend.domain.study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skhumeet.backend.domain.BaseTime;
import skhumeet.backend.domain.member.Member;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    //작성자
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member writer;

    //포스트
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 내용
    @Column(name = "context", nullable = false)
    private String context;

    //대댓글은 언급 형식
    //댓글
    @ManyToOne
    @JoinColumn(name = "parent_comment")
    private Comment parentComment;

    //자식댓글
    @OneToMany(mappedBy = "parentComment", orphanRemoval = true)
    private List<Comment> childComments;
}
