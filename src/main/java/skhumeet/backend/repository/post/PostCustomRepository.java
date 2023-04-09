package skhumeet.backend.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skhumeet.backend.domain.dto.PostDTO;

public interface PostCustomRepository {
    Page<PostDTO.Response> findByKeyword(Pageable pageable, String keyword);
}
