package ru.practicum.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;
import ru.practicum.util.Patterns;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class CommentMapper {
    public Comment toComment(User user, Event event, NewCommentDto commentDto) {
        return Comment.builder()
                .creator(user)
                .dateCreate(LocalDateTime.now())
                .text(commentDto.getText())
                .event(event).build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .creator(comment.getCreator().getName())
                .event(comment.getEvent().getTitle())
                .text(comment.getText())
                .dateCreate(comment.getDateCreate()
                        .format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN))).build();
    }

}