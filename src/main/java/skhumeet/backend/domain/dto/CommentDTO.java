package skhumeet.backend.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import skhumeet.backend.domain.study.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDTO {

    @Schema(name = "CommentDTO.Request (댓글 작성 요청 DTO)")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @Schema(description = "Post ID (게시글 ID)", defaultValue = "1")
        private Long PostId;
        @Schema(description = "Context (댓글 내용)", defaultValue = "Test comment context")
        private String context;
    }

    @Schema(name = "CommentDTO.Response (댓글 API 응답 DTO)")
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        @Schema(description = "Comment ID (댓글 ID)")
        private Long id;
        @Schema(description = "Writer (작성자)")
        private String writer;
        @Schema(description = "Context (댓글 내용)")
        private String context;
        @Schema(description = "Child comments (대댓글 목록)")
        private List<Response> childComments;
        @Schema(description = "Modified Date (수정된 날짜)")
        private LocalDateTime modifiedDate;

        public Response(Comment comment) {
            this.id = comment.getId();
            this.writer = comment.getWriter().getName();
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
    @Schema(name = "CommentDTO.Reply (대댓글 작성 요청 DTO)")
    public static class Reply {
        @Schema(description = "Post ID (게시글 ID)", defaultValue = "1")
        private Long postId;
        @Schema(description = "Parent comment ID (부모 댓글 ID)", defaultValue = "1")
        private Long parentCommentId;
        @Schema(description = "Writer (작성자)", defaultValue = "김회대")
        private String writer;
        @Schema(description = "Context (댓글 내용)", defaultValue = "Test comment context")
        private String context;
    }

    @Schema(name = "CommentDTO.Update (댓글 수정 요청 DTO)")
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        @Schema(description = "Context (댓글 내용)", defaultValue = "Test comment context")
        private String context;
    }
}
