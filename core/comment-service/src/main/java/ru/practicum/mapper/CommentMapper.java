package main.java.ru.practicum.mapper;

import main.java.ru.practicum.dto.EventShortDto;
import main.java.ru.practicum.dto.UserShortDto;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.dto.ResponseCommentDto;
import main.java.ru.practicum.dto.RequestCommentDto;
import main.java.ru.practicum.persistence.entity.Comment;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "newCommentDto.text")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "edited", ignore = true)
    Comment toComment(RequestCommentDto newCommentDto, UserShortDto author, EventShortDto event);

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "created", source = "comment.created", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "edited", source = "comment.edited", qualifiedByName = "localDateTimeToString")
    ResponseCommentDto toCommentDto(Comment comment, UserShortDto author, EventShortDto event);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime time) {
        if (time == null) {
            return null;
        }

        return time.format(FORMATTER);
    }
}