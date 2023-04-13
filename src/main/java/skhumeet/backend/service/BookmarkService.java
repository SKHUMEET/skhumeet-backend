package skhumeet.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skhumeet.backend.domain.dto.BookmarkDTO;
import skhumeet.backend.domain.dto.HttpResponseDTO;
import skhumeet.backend.domain.dto.ImageDTO;
import skhumeet.backend.domain.member.Member;
import skhumeet.backend.domain.study.Bookmark;
import skhumeet.backend.domain.study.Post;
import skhumeet.backend.repository.BookmarkRepository;
import skhumeet.backend.repository.MemberRepository;
import skhumeet.backend.repository.post.PostRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final BookmarkRepository bookmarkRepository;

    // Create (북마크 생성)
    @Transactional
    public ResponseEntity<HttpResponseDTO> saveBookmark(String username, Long postId) {
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        Bookmark bookmark = Bookmark.builder()
                .member(member)
                .post(post)
                .build();
        post.updateIsBookmarked(true);
        bookmarkRepository.save(bookmark);
        postRepository.save(post);
        return ResponseEntity.ok(new HttpResponseDTO("Bookmarking success", new BookmarkDTO.Response(bookmark)));
    }

    // Read (북마크 목록 조회)
    public ResponseEntity<HttpResponseDTO> findBookmarkByMember(Pageable pageable, String username) {
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        Page<BookmarkDTO.Response> bookmarks = bookmarkRepository.findByMember(pageable, member).map(BookmarkDTO.Response::new);
        return ResponseEntity.ok(new HttpResponseDTO("Member's bookmarks find success", bookmarks));
    }

    // Delete (북마크 제거)
    @Transactional
    public ResponseEntity<HttpResponseDTO> deleteBookmark(String username, Long postId) {
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        try {
            Bookmark bookmark = bookmarkRepository.findByMemberAndPost(member, post)
                    .orElseThrow(() -> new NoSuchElementException("Bookmark not found"));
            if (bookmark.getMember().getLoginId().equals(username)) {
                bookmarkRepository.delete(bookmark);
            } else {
                throw new AuthorizationServiceException("Unauthorized access");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(
                    new HttpResponseDTO(e.getMessage(), "Fail to delete Bookmark"),
                    HttpStatus.BAD_REQUEST
            );
        }
        return ResponseEntity.ok(new HttpResponseDTO("Delete success"));
    }
}
