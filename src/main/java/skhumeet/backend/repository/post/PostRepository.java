package skhumeet.backend.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skhumeet.backend.domain.member.Member;
import skhumeet.backend.domain.study.Category;
import skhumeet.backend.domain.study.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    Page<Post> findByCategory(Pageable pageable, Category category);
    Page<Post> findByAuthor(Pageable pageable, Member member);
}
