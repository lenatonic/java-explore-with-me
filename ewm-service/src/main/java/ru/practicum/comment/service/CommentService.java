package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long userId, Long eventId, NewCommentDto commentDto);

    List<CommentDto> findCommentsUser(Long userId);

//    List<CommentDto> findAllCommentsUserByEvent(Long userId, Long eventId);

    CommentDto findComment(Long userId, Long commentId);

    void deleteComment(Long userId, Long commentId);

    CommentDto upComment(Long userId, Long commentId, NewCommentDto commentDto);
}