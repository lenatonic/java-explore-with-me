package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class CommentController {
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/comments/{eventId}")
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @RequestBody @Valid NewCommentDto commentDto) {
        CommentDto ans = commentService.addComment(userId, eventId, commentDto);
        log.info("Пользователь Id = {} оставляет комментарий к событию id = {}.", userId, eventId);
        return ans;
    }

    @PatchMapping("/comments/{commentId}")
    public CommentDto commentDto(@PathVariable Long userId,
                                 @PathVariable Long commentId,
                                 @RequestBody @Valid NewCommentDto commentDto) {
        CommentDto ans = commentService.updateComment(userId, commentId, commentDto);
        log.info("Пользователь id = {}, отредактировал комментарий id = {}.", userId, commentId);
        return ans;
    }

    @GetMapping("/comments")
    public List<CommentDto> findAllCommentsUser(@PathVariable Long userId) {
        List<CommentDto> ans = commentService.findCommentsUser(userId);
        log.info("Получения списка комментариев пользователя id = {}.", userId);
        return ans;
    }

    @GetMapping("/comments/{commentId}")
    public CommentDto findComment(@PathVariable Long userId,
                                  @PathVariable Long commentId) {
        CommentDto ans = commentService.findComment(userId, commentId);
        log.info("Получение комментария id = {}.", commentId);
        return ans;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
        log.info("Комментарий с id = {} удалён пользователем id = {}.", commentId, userId);
    }
}