package skhumeet.backend.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import skhumeet.backend.domain.study.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CommentDTO.Request")
    public static class Request {
        @Schema(description = "Post Id", defaultValue = "1")
        private Long PostId;
        @Schema(description = "Context", defaultValue = "Test comment context")
        private String context;
    }

    @Getter
    @Schema(name = "CommentDTO.Response")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        @Schema(description = "Comment Id")
        private Long id;
        @Schema(description = "Nickname")
        private String author;
        @Schema(description = "Origin writer nickname")
        private String writer;
        @Schema(description = "Context")
        private String context;
        @Schema(description = "Child comments")
        private List<Response> childComments;
        @Schema(description = "Modified Date")
        private LocalDateTime modifiedDate;

        public Response(Comment comment) {
            this.id = comment.getId();
            this.author = comment.getWriter().getNickname();
            this.writer = comment.getWriter().getNickname();
            this.context = comment.getContext();
            this.childComments = comment.getChildComments().stream()
                    .map(CommentDTO.Response::new).collect(Collectors.toList());
            this.modifiedDate = comment.getModifiedDate();
        }
    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CommentDTO.Reply")
    public static class Reply {
        @Schema(description = "Post Id", defaultValue = "1")
        private Long postId;
        @Schema(description = "Parent comment Id", defaultValue = "1")
        private Long parentCommentId;
        @Schema(description = "Username(Nickname)", defaultValue = "testuser")
        private String writer;
        @Schema(description = "Context", defaultValue = "Test comment context")
        private String context;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CommentDTO.Update")
    public static class Update {
        @Schema(description = "Context", defaultValue = "Test comment context")
        private String context;
    }
}
