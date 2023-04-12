package skhumeet.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skhumeet.backend.domain.study.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
