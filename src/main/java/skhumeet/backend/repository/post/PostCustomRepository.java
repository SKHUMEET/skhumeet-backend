package skhumeet.backend.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skhumeet.backend.domain.study.Post;

public interface PostCustomRepository {
    Page<Post> findByKeyword(Pageable pageable, String keyword);
}
