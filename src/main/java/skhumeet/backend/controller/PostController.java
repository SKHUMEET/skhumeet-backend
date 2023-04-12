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
import skhumeet.backend.domain.dto.PostDTO;
import skhumeet.backend.service.PostService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Tag(name = "MainPost", description = "API for main post")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class PostController {
    private final PostService postService;

    //Create
    @Operation(
            summary = "create post",
            description = "Create post. Category List : HANSOTBAB, EOULLIM, STUDY, CLUB, CONTEST, DEPARTMENT_EVENT, ETC"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping(value = "/new")
    public ResponseEntity<PostDTO.Response> save(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody PostDTO.Request request) throws IOException {
        return ResponseEntity.ok(postService.save(userDetails.getUsername(), request));
    }

    //Read
    // id별 post 불러오기
    @Operation(summary = "find main post by id", description = "Read main post from database by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/post")
    public ResponseEntity<PostDTO.Response> findById(@RequestParam("id") Long id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    //해당 유저의 post 불러오기
    @Operation(summary = "find main post by current member", description = "Read main post from database by current member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/post/member")
    public Page<PostDTO.Response> findByMember(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestParam("page") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber == 0 ? 0 : pageNumber-1, 9, Sort.by("id").descending());
        return postService.findByMember(pageable, userDetails.getUsername());
    }

//    //postid로 view 증가
//    @Operation(summary = "View Increase", description = "View increments each time a post is viewed")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "OK"),
//            @ApiResponse(responseCode = "400", description = "Bad Request")
//    })
//    @PatchMapping("/{id}/view")
//    public ResponseEntity<Void> increaseViewCount(@PathVariable Long id, HttpServletResponse response, HttpServletRequest request) {
//        return postService.increaseViewCount(id, response, request);
//    }



    //카테고리별 조회
    @Operation(summary = "find main post by category", description = "Read main posts from database by category")
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

    //키워드 검색
    @Operation(summary = "find main post by keyword", description = "Read main posts from database by keyword in title or context")
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

    //모든 post 조회
    @Operation(summary = "find main posts", description = "Read main posts from database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/all")
    public Page<PostDTO.Response> findAll(@RequestParam("page") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber == 0 ? 0 : pageNumber-1, 9, Sort.by("id").descending());
        return postService.findAll(pageable);
    }

    //Update
    @Operation(summary = "Update main post", description = "Update main post. Status List : ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PatchMapping(value = "/post")
    public ResponseEntity<PostDTO.Response> update(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestBody PostDTO.Update update,
                                                       @RequestParam("id") Long id) throws IOException {
        return ResponseEntity.ok(postService.update(userDetails.getUsername(), update, id));
    }

    //Delete
    @Operation(summary = "Delete main post", description = "Delete main post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @DeleteMapping("/post")
    public ResponseEntity<String> delete(@RequestParam("id") Long id) {
        return postService.delete(id);
    }
}
