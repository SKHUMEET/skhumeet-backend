package skhumeet.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import skhumeet.backend.domain.member.Member;
import skhumeet.backend.domain.study.Bookmark;
import skhumeet.backend.domain.study.Post;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Page<Bookmark> findByMember(Pageable pageable, Member member);
    Optional<Bookmark> findByMemberAndPost(Member member, Post post);
}
