package ru.yandex.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.comment.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByEventIdOrderByCreatedOnDesc(Integer eventId, Pageable pageable);

    Optional<Comment> findByEventIdAndAuthorId(Integer eventId, Integer userId);

    List<Comment> findTop10ByEventIdOrderByCreatedOnDesc(Integer eventId);

    List<Comment> findAllByEventIdOrderByCreatedOnDesc(Integer eventId);

    boolean existsCommentById(Integer id);
}
