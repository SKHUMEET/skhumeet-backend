package skhumeet.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skhumeet.backend.domain.dto.CommentDTO;
import skhumeet.backend.domain.study.Comment;
import skhumeet.backend.domain.study.Post;
import skhumeet.backend.repository.CommentRepository;
import skhumeet.backend.repository.MemberRepository;
import skhumeet.backend.repository.post.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    // Create
    @Transactional
    public CommentDTO.Response save(String username, CommentDTO.Request request) {
        Comment comment = Comment.builder()
                .post(postRepository.findById(request.getPostId())
                        .orElseThrow(() -> new NoSuchElementException("Post not found")))
                .writer(memberRepository.findByLoginId(username)
                        .orElseThrow(() -> new NoSuchElementException("Member not found")))
                .context(request.getContext())
                .childComments(new ArrayList<>())
                .build();
        return new CommentDTO.Response(commentRepository.save(comment));
    }

    public CommentDTO.Response reply(String username, CommentDTO.Reply reply) {
        Comment comment = Comment.builder()
                .post(postRepository.findById(reply.getPostId())
                        .orElseThrow(() -> new NoSuchElementException("Post not found")))
                .parentComment(commentRepository.findById(reply.getParentCommentId())
                        .orElseThrow(() -> new NoSuchElementException("Comment not found")))
                .writer(memberRepository.findByLoginId(username)
                        .orElseThrow(() -> new NoSuchElementException("Member not found")))
                .context(reply.getContext())
                .childComments(new ArrayList<>())
                .build();
        return new CommentDTO.Response(commentRepository.save(comment));
    }

    // Read
    public List<CommentDTO.Response> findByPost(Pageable pageable, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        List<Comment> comments = commentRepository.findByPost(pageable, post);
        List<CommentDTO.Response> responses = new ArrayList<>();
        comments.forEach(comment -> {
            if(comment.getParentComment() == null) {
                responses.add(new CommentDTO.Response(comment));
            }
        });
        return responses;
    }

    private Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
    }

    //Update
    public CommentDTO.Response update(String username, CommentDTO.Update update, Long postId, Long id) {
        postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        Comment oldComment = this.findById(id);
        Comment newComment;
        if(oldComment.getWriter().getLoginId().equals(username)) {
            newComment = Comment.builder()
                    .id(id)
                    .post(oldComment.getPost())
                    .writer(oldComment.getWriter())
                    .context(update.getContext())
                    .childComments(oldComment.getChildComments())
                    .build();
        } else {
            throw new AuthorizationServiceException("Unauthorized access");
        }
        commentRepository.save(newComment);
        return new CommentDTO.Response(this.findById(newComment.getId()));
    }

    // Delete
    public ResponseEntity<String> delete(Long id) {
        Comment comment = this.findById(id);
        commentRepository.delete(comment);
        return ResponseEntity.ok("Delete success");
    }
}