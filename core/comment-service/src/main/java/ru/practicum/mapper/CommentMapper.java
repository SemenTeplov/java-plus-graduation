package main.java.ru.practicum.mapper;

import main.dto.EventShortDto;
import main.dto.UserShortDto;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.dto.CommentDto;
import main.java.ru.practicum.dto.NewCommentDto;
import main.java.ru.practicum.persistence.entity.Comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "newCommentDto.text")
    @Mapping(target = "author", source = "author.id")
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "edited", ignore = true)
    Comment toComment(NewCommentDto newCommentDto, UserShortDto author, EventShortDto event);

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "created", source = "comment.created", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "edited", source = "comment.edited", qualifiedByName = "localDateTimeToString")
    CommentDto toCommentDto(Comment comment, UserShortDto author, EventShortDto event);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime time) {
        if (time == null) {
            return null;
        }

        return time.format(FORMATTER);
    }
}