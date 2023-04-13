package skhumeet.backend.repository.post;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import skhumeet.backend.domain.dto.PostDTO;
import skhumeet.backend.domain.study.Post;

import java.util.List;

import static skhumeet.backend.domain.study.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Post> findByKeyword(Pageable pageable, String keyword) {
        List<Post> searchResult = jpaQueryFactory
                .select(post)
                .from(post)
                .where(post.title.containsIgnoreCase(keyword)
                        .or(post.context.containsIgnoreCase(keyword))
                )
                .orderBy(
                        post.id.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(post.title.containsIgnoreCase(keyword)
                        .or(post.context.containsIgnoreCase(keyword))
                ).orderBy(
                        post.id.desc()
                );

        return PageableExecutionUtils.getPage(searchResult, pageable, countQuery::fetchOne);
    }
}
