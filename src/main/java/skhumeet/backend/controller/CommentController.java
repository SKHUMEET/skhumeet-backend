package skhumeet.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import skhumeet.backend.domain.dto.CommentDTO;
import skhumeet.backend.service.CommentService;

import java.util.List;

@Tag(name = "Comment API (댓글 관련 API)", description = "API for Comment CRUD (댓글 CRUD를 위한 API)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    // Create (댓글 작성)
    @Operation(
            summary = "Create Comment API (댓글 작성 API)",
            description = """
                    Create comment. Authorize needed.<br/>
                    댓글 작성. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/new")
    public ResponseEntity<CommentDTO.Response> save(@AuthenticationPrincipal UserDetails userDetails,
                                                    @RequestBody CommentDTO.Request request) {
        return ResponseEntity.ok(commentService.save(userDetails.getUsername(), request));
    }

    // 대댓글
    @Operation(
            summary = "Create Reply (대댓글 작성 API)",
            description = """
                    Create reply comment. Authorize needed.<br/>
                    대댓글 작성. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/reply")
    public ResponseEntity<CommentDTO.Response> reply(@AuthenticationPrincipal UserDetails userDetails,
                                                     @RequestBody CommentDTO.Reply reply) {
        return ResponseEntity.ok(commentService.reply(userDetails.getUsername(), reply));
    }

    // Read (댓글 조회)
    @Operation(
            summary = "Read Comments API (댓글 목록 조회 API)",
            description = """
                    Read comments by post ID. Authorize needed.<br/>
                    게시글 ID 값을 기준으로 댓글 목록을 조회. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/{postId}/page/{pageNumber}")
    public List<CommentDTO.Response> findByPostId(@PathVariable("postId") Long postId,
                                                     @PathVariable("pageNumber") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber == 0 ? 0 : pageNumber-1, 20, Sort.by("id").descending());
        return commentService.findByPost(pageable, postId);
    }

    // Update (댓글 수정)
    @Operation(
            summary = "Update Comment API (댓글 수정 API)",
            description = """
                    Update comment by post ID and comment ID. Authorize needed.<br/>
                    게시글 ID와 댓글 ID를 기준으로 댓글 수정. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PatchMapping("/{postId}/comment/{id}")
    public ResponseEntity<CommentDTO.Response> update(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody CommentDTO.Update update,
                                                      @PathVariable("postId") Long postId,
                                                      @PathVariable("id") Long id) {
        return ResponseEntity.ok(commentService.update(userDetails.getUsername(), update, postId, id));
    }

    // Delete (댓글 삭제)
    @Operation(
            summary = "Delete Comment API (댓글 삭제 API)",
            description = """
                    Delete comment by comment ID. Authorize needed.<br/>
                    댓글 ID 기준으로 댓글 삭제. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        return commentService.delete(id);
    }

}
