package ru.practicum.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentDto {
    private Long id;
    private String creator;
    private String event;
    private String text;
    private String dateCreate;
}
