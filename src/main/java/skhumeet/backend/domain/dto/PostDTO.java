package skhumeet.backend.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import skhumeet.backend.domain.study.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostDTO {
    @Schema(name = "PostDTO.Request (게시글 작성 요청 DTO)")
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @Schema(description = "Title (제목)", defaultValue = "Test Title")
        private String title;
        @Schema(description = "Category (카테고리)", defaultValue = "Hansotbab")
        private String category;
        @Schema(description = "Status (카테고리)", defaultValue = "Recruiting")
        private String status;
        @Schema(description = "Contact (연락처)", defaultValue = "Kakao OpenChat")
        private String contact;
        @Schema(description = "End Date (기한)", defaultValue = "2023-03-31")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime endDate;
        @Schema(description = "View count (조회수)", defaultValue = "1")
        private int view;
        @Schema(description = "Context (내용)", defaultValue = "Test Context")
        private String context;
        @Schema(description = "Input paths that returned by Image API", defaultValue = "")
        private List<String> images;
    }

    @Schema(name = "PostDTO.Response (게시글 API 응답 DTO)")
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        @Schema(description = "ID (게시글 ID)")
        private Long id;
        @Schema(description = "Category (카테고리)")
        private String category;
        @Schema(description = "Status (상태)")
        private String status;
        @Schema(description = "Title (제목)")
        private String title;
        @Schema(description = "Member (작성자)")
        private String member;
        @Schema(description = "MemberNumber (학번 또는 교번)")
        private String memberNumber;
        @Schema(description = "EndDate (기한)")
        private LocalDateTime endDate;
        @Schema(description = "Created Date (작성일)")
        private LocalDateTime createdDate;
        @Schema(description = "Contact (연락처)")
        private String contact;
        @Schema(description = "View (조회수)", defaultValue = "0")
        private int view;
        @Schema(description = "Context (내용)")
        private String context;
        @Schema(description = "Image files (이미지 목록)", defaultValue = "")
        private List<String> images;

        @QueryProjection
        public Response(Post post) {
            this.id = post.getId();
            this.category = post.getCategory().toString();
            this.status = post.getStatus().toString();
            this.title = post.getTitle();
            this.member = post.getAuthor().getName();
            this.memberNumber = post.getAuthor().getMemberNumber();
            this.endDate = post.getEndDate();
            this.createdDate = post.getCreatedDate();
            this.contact = post.getContact();
            this.view = post.getView();
            this.context = post.getContext();
            this.images = new ArrayList<>();
            if (post.getImages() != null) {
                images.addAll(post.getImages());
            }
        }
    }

    @Schema(name = "PostDTO.Update (게시글 수정 요청 DTO)")
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        @Schema(description = "Title (제목)", defaultValue = "Modified Title")
        private String title;
        @Schema(description = "Category (카테고리)", defaultValue = "Eoullim")
        private String category;
        @Schema(description = "Status (상태)", defaultValue = "Recruitment Deadline")
        private String status;
        @Schema(description = "Contact (연락처)", defaultValue = "Google Form")
        private String contact;
        @Schema(description = "EndDate (기한)", defaultValue = "2023-04-13")
        private LocalDateTime endDate;
        @Schema(description = "Context (내용)", defaultValue = "Modified Context")
        private String context;
        @Schema(description = "Image files (이미지 목록)", defaultValue = "")
        private List<String> images;
    }
}
