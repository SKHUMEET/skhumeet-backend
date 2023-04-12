package skhumeet.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import skhumeet.backend.domain.dto.HttpResponseDTO;
import skhumeet.backend.domain.dto.PostDTO;
import skhumeet.backend.service.BookmarkService;
import skhumeet.backend.service.PostService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Tag(name = "Post API (게시글 관련 API)", description = "API for Post CRUD (게시글 CRUD를 위한 API)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    private final BookmarkService bookmarkService;

    // Create (게시글 작성)
    @Operation(
            summary = "Create Post API (게시글 작성 API)",
            description = """
                    Create Post. Authorize needed.<br/>
                    게시글 작성. 로그인(토큰) 필요.
                    
                    Category List : HANSOTBAB, EOULLIM, STUDY, CLUB, CONTEST, DEPARTMENT_EVENT, ETC<br/>
                    카테고리 목록 : 한솥밥, 어울림, 스터디, 동아리, 경진대회, 학부 행사, 기타
                    
                    카테고리 목록의 카테고리 값들은 <strong>대소문자를 구분하지 않음<strong>.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/new")
    public ResponseEntity<PostDTO.Response> save(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody PostDTO.Request request) throws IOException {
        return ResponseEntity.ok(postService.save(userDetails.getUsername(), request));
    }

    // Read (게시글 조회)
    // ID 기준 Post 조회
    @Operation(
            summary = "Read Post by ID API (ID 기준 게시글 조회 API)",
            description = """
                    Read main post from database by id. Authorize needed.<br/>
                    데이터베이스에서 ID 값을 통해 게시글 단건 조회. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping
    public ResponseEntity<PostDTO.Response> findById(@RequestParam("id") Long id,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) {
        return ResponseEntity.ok(postService.findById(id, request, response));
    }

    // Member가 작성한 Post 조회
    @Operation(
            summary = "Read Post by Member API (Member 기준 게시글 조회 API)",
            description = """
                    Read posts from database by current member. Authorize needed.<br/>
                    데이터베이스에서 현재 로그인 된 Member가 작성한 게시글 목록 조회.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/member")
    public Page<PostDTO.Response> findByMember(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestParam("page") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber == 0 ? 0 : pageNumber-1, 9, Sort.by("id").descending());
        return postService.findByMember(pageable, userDetails.getUsername());
    }

    // 카테고리별 조회
    @Operation(
            summary = "Read Post by Category API (Category 기준 게시글 조회 API)",
            description = """
                    Read posts from database by category. Authorize needed.<br/>
                    데이터베이스에서 카테고리 기준 게시글 목록 조회. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/category/{category}")
    public Page<PostDTO.Response> findByCategory(@PathVariable("category") String category,
                                                     @RequestParam("page") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber == 0 ? 0 : pageNumber-1, 9, Sort.by("id").descending());
        return postService.findByCategory(pageable, category);
    }

    // 키워드 검색
    @Operation(
            summary = "Find Post by Keyword API (Keyword 기준 게시글 조회 API)",
            description = """
                    Read posts from database by keyword in title or context. Authorize needed.<br/>
                    데이터베이스에서 제목과 내용의 키워드 기준 게시글 목록 조회. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/keyword/{keyword}")
    public Page<PostDTO.Response> findByKeyword(@PathVariable("keyword") String keyword,
                                                    @RequestParam("page") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber == 0 ? 0 : pageNumber-1, 9, Sort.by("id").descending());
        return postService.findByKeyword(pageable, keyword);
    }

    // 모든 Post 조회
    @Operation(
            summary = "Read all Posts API (모든 게시글 조회 API)",
            description = """
                    Read all posts from database. Authorize needed.<br/>
                    데이터베이스에서 모든 게시글 목록 조회. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/all")
    public Page<PostDTO.Response> findAll(@RequestParam("page") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber == 0 ? 0 : pageNumber-1, 9, Sort.by("id").descending());
        return postService.findAll(pageable);
    }

    // Update (게시글 수정)
    @Operation(
            summary = "Update Post API (게시글 수정 API)",
            description = """
                    Update post. Authorize needed.<br/>
                    게시글 수정. 로그인(토큰) 필요.
                    
                    Status List : RECRUITING, RECRUITMENT_DEADLINE, PROMOTION, ACTIVITY<br/>
                    게시글 상태 목록 : 모집 중, 모집 완료, 홍보, 활동
                    
                    게시글 상태 목록의 상태값들은 <strong>대소문자를 구분하지 않음</strong>.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PatchMapping
    public ResponseEntity<PostDTO.Response> update(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestBody PostDTO.Update update,
                                                       @RequestParam("id") Long id) throws IOException {
        return ResponseEntity.ok(postService.update(userDetails.getUsername(), update, id));
    }

    // Delete (게시글 삭제)
    @Operation(
            summary = "Delete Post API (게시글 삭제 API)",
            description = """
                    Delete post by Post ID. Authorize needed.<br/>
                    게시글의 ID 값을 기준으로 게시글 삭제 요청. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam("id") Long id) {
        return postService.delete(id);
    }

    // ETC (게시글 관련 기타 API)
    @Operation(
            summary = "Post Bookmark API (게시글 북마크 API)",
            description = """
                    Delete post by Post ID. Authorize needed.<br/>
                    게시글의 ID 값을 기준으로 게시글 삭제 요청. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/bookmark")
    public ResponseEntity<HttpResponseDTO> bookmarking(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("id") Long id) {
        return bookmarkService.saveBookmark(userDetails.getUsername(), id);
    }
}
