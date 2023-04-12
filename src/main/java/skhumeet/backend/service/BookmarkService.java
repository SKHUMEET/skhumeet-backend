package skhumeet.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import skhumeet.backend.domain.dto.HttpResponseDTO;
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
    public ResponseEntity<HttpResponseDTO> saveBookmark(String username, Long postId) {
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        Bookmark bookmark = Bookmark.builder()
                .member(member)
                .post(post)
                .build();
        bookmarkRepository.save(bookmark);
        return ResponseEntity.ok(new HttpResponseDTO("Bookmarking success", bookmark));
    }
}
