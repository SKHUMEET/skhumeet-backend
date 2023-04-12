package skhumeet.backend.domain.study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skhumeet.backend.domain.member.BaseTime;
import skhumeet.backend.domain.member.Member;
import skhumeet.backend.util.StringListConverter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    //작성자
    @ManyToOne
    @JoinColumn(name = "post_author")
    private Member author;

    //카테고리
    @Enumerated(EnumType.STRING)
    private Category category;

    //상태
    @Enumerated(EnumType.STRING)
    private Status status;

    //연락 방법
    @Column(name = "contact", nullable = false)
    private String contact;

    //마감일
    @Column(name="endDate")
    private LocalDateTime endDate;

    //조회수
    @Column(name="view")
    private int view;

    //제목
    @Column(name = "title")
    private String title;

    //내용
    @Size(min = 1, max = 65534)
    @Column(name = "context", columnDefinition = "TEXT", nullable = false)
    private String context;

    //이미지
    @Column(name = "images", length = 2048)
    @Convert(converter = StringListConverter.class)
    private List<String> images;


    public void increaseViews() {
        this.view++;
    }
}
