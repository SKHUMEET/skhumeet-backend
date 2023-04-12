package skhumeet.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skhumeet.backend.domain.dto.ImageDTO;
import skhumeet.backend.domain.dto.PostDTO;
import skhumeet.backend.domain.member.Member;
import skhumeet.backend.domain.study.Category;
import skhumeet.backend.domain.study.Post;
import skhumeet.backend.domain.study.Status;
import skhumeet.backend.repository.MemberRepository;
import skhumeet.backend.repository.post.PostRepository;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final ImageService imageService;
    private final EntityManager entityManager;

    // Create
    public PostDTO.Response save(String username, PostDTO.Request request) throws IOException {
        Post post = Post.builder()
                .title(request.getTitle())
                .author(memberRepository.findByLoginId(username)
                        .orElseThrow(() -> new NoSuchElementException("Member not found")))
                .category(Category.valueOf(request.getCategory().toUpperCase()))
                .status(Status.valueOf(request.getStatus().toUpperCase()))
                .endDate(request.getEndDate())
                .contact(request.getContact())
                .view(request.getView())
                .context(request.getContext())
                .images(new ArrayList<>())
                .build();
        saveWithImage(post, request.getImages());
        return new PostDTO.Response(post);
    }

    // Read
    @Transactional(readOnly = true)
    public Page<PostDTO.Response> findAll(Pageable pageable) {
        return postRepository.findAll(pageable).map(post -> {
            if (post.getImages() != null) {
                post.getImages().replaceAll(
                        storedImageName -> imageService.findByStoredImageName(storedImageName).getImagePath()
                );
            }
            return post;
        }).map(PostDTO.Response::new);
    }

    @Transactional
    public PostDTO.Response findById(Long id, HttpServletRequest request, HttpServletResponse response) {
        return new PostDTO.Response(postRepository.findById(id).map(post -> {
            if (post.getImages() != null) {
                post.getImages().replaceAll(
                        storedImageName -> imageService.findByStoredImageName(storedImageName).getImagePath()
                );
            }
            countViews(post, request, response);
            return post;
        }).orElseThrow(() -> new NoSuchElementException("There is no Post with this ID")));
    }

    @Transactional(readOnly = true)
    public Page<PostDTO.Response> findByMember(Pageable pageable, String username) {
        Member member = memberRepository.findByLoginId(username) //id로 user 찾기
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        return postRepository.findByAuthor(pageable, member).map(post -> {
            if (post.getImages() != null) {
                post.getImages().replaceAll(
                        storedImageName -> imageService.findByStoredImageName(storedImageName).getImagePath()
                );
            }
            return post;
        }).map(PostDTO.Response::new);
    }

    @Transactional(readOnly = true)
    public Page<PostDTO.Response> findByCategory(Pageable pageable, String category) {
        return postRepository.findByCategory(pageable, Category.valueOf(category.toUpperCase()))
                .map(post -> {
                    if (post.getImages() != null) {
                        post.getImages().replaceAll(
                                storedImageName -> imageService.findByStoredImageName(storedImageName).getImagePath()
                        );
                    }
                    return post;
                }).map(PostDTO.Response::new);
    }

    @Transactional(readOnly = true)
    public Page<PostDTO.Response> findByKeyword(Pageable pageable, String keyword) {
        return postRepository.findByKeyword(pageable, keyword).map(post -> {
            if (post.getImages() != null) {
                post.getImages().replaceAll(
                        storedImageName -> imageService.findByStoredImageName(storedImageName).getImagePath()
                );
            }
            return post;
        });
    }

    // Update
    public PostDTO.Response update(String username, PostDTO.Update update, Long id) throws IOException {
        Post oldPost = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("There is no Post with this ID"));
        Post newPost;
        if (username.equals(oldPost.getAuthor().getLoginId())) {
            newPost = Post.builder()
                    .id(id)
                    .title(update.getTitle())
                    .author(oldPost.getAuthor())
                    .category(Category.valueOf(update.getCategory().toUpperCase()))
                    .endDate(update.getEndDate())
                    .contact(update.getContact())
                    .context(update.getContext())
                    .images(new ArrayList<>())
                    .build();
        } else {
            throw new AuthorizationServiceException("Unauthorized access");
        }
        saveWithImage(newPost, update.getImages());
        return new PostDTO.Response(newPost);
    }

    // Delete
    @Transactional
    public ResponseEntity<String> delete(Long id) {
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Main post not found"));
            if (post.getImages() != null) {
                for (String imageName : post.getImages()) {
                    ImageDTO.Response image = imageService.findByStoredImageName(imageName);
                    imageService.deleteImage("main/", image.getImagePath());
                }
            }
            postRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Exception occurred", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("Delete success");
    }

    // Util
    private void saveWithImage(Post post, List<String> images) {
        if (images != null) {
            for (String imagePath : images) {
                ImageDTO.Response image = imageService.findByImagePath(imagePath);
                post.getImages().add(image.getStoredImageName());
            }
        }
        postRepository.saveAndFlush(post);
        entityManager.detach(post);
        if (post.getImages() != null) {
            post.getImages().replaceAll(storedImageName -> imageService.findByStoredImageName(storedImageName).getImagePath());
        }
    }

    // 게시글 조회수 연산
    private void countViews(Post post, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = null;
        Cookie[] cookies = request.getCookies();
        if (post != null) {
            if (cookies != null) {
                for (Cookie oldCookie : cookies) {
                    if (oldCookie.getName().equals("postViews")) {
                        cookie = oldCookie;
                        break;
                    }
                }
            }
            if (cookie != null) {
                if (!cookie.getValue().contains("POST[" + post.getId() + "]")) {
                    post.updateViews();
                    postRepository.save(post);
                    cookie.setValue(cookie.getValue() + "POST[" + post.getId() + "]");
                }
            } else {
                post.updateViews();
                postRepository.save(post);
                cookie = new Cookie("postViews", "POST[" + post.getId() + "]");
            }
            response.addCookie(setCookieValue(cookie));
        }
    }

    // 쿠키 설정
    private Cookie setCookieValue(Cookie cookie) {
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        return cookie;
    }
}
