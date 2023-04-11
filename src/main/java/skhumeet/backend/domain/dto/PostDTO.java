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

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PostDTO.Request")
    public static class Request {
        @Schema(description = "Title", defaultValue = "Test Title")
        private String title;
        @Schema(description = "Category", defaultValue = "Hansotbab")
        private String category;
        @Schema(description = "Contact", defaultValue = "Kakao OpenChat")
        private String contact;
        @Schema(description = "End Date", defaultValue = "2023-03-31")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime endDate;
        @Schema(description = "View count", defaultValue = "1")
        private Long view;
        @Schema(description = "Context", defaultValue = "Test Context")
        private String context;
        @Schema(description = "Input paths that returned by Image API", defaultValue = "")
        private List<String> images;
    }

    @Getter
    @Schema(name = "PostDTO.Response")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        @Schema(description = "ID")
        private Long id;
        @Schema(description = "Category")
        private String category;
        @Schema(description = "Title")
        private String title;
        @Schema(description = "Nickname")
        private String nickname;
        @Schema(description = "EndDate")
        private LocalDateTime endDate;
        @Schema(description = "Created Date")
        private LocalDateTime createDate;
        @Schema(description = "Contact")
        private String contact;
        @Schema(description = "View")
        private Long view;
        @Schema(description = "Context")
        private String context;
        @Schema(description = "Image files", defaultValue = "")
        private List<String> images;


        @QueryProjection
        public Response(Post post) {
            this.id = post.getId();
            this.category = post.getCategory().toString();
            this.title = post.getTitle();
            this.nickname = post.getAuthor().getNickname();
            this.endDate = post.getEndDate();
            this.createDate = post.getCreatedDate();
            this.contact = post.getContact().toString();
            this.view = post.getView();
            this.context = post.getContext();
            this.images = new ArrayList<>();
            if (post.getImages() != null) {
                images.addAll(post.getImages());
            }
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PostDTO.Update")
    public static class Update {
        @Schema(description = "Title", defaultValue = "Modified Title")
        private String title;
        @Schema(description = "Category", defaultValue = "Eoullim")
        private String category;
        @Schema(description = "Contact", defaultValue = "Google Form")
        private String contact;
        @Schema(description = "EndDate", defaultValue = "2023-04-13")
        private LocalDateTime endDate;
        @Schema(description = "Context", defaultValue = "Modified Context")
        private String context;
        @Schema(description = "Image files", defaultValue = "")
        private List<String> images;
    }
}
