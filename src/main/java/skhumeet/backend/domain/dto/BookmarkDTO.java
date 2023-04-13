package skhumeet.backend.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import skhumeet.backend.domain.study.Bookmark;

public class BookmarkDTO {
    @Schema(name = "PostDTO.Response (게시글 API 응답 DTO)")
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        private Long id;
        private PostDTO.Response post;

        public Response(Bookmark bookmark) {
            this.id = bookmark.getId();
            this.post = new PostDTO.Response(bookmark.getPost());
        }
    }
}
