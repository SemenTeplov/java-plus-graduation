package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Exceptions {

    public static final String EXCEPTION_NOT_ILLEGAL_ARGUMENT = "Используется не допустимое значение";
    public static final String EXCEPTION_DATE_MISMATCH = "Дата не соответствует требованиям 409";
    public static final String EXCEPTION_INTERNAL_SERVER = "Внутренняя ошибка сервера. Подождите несколько минут и попробуйте снова.";
    public static final String EXCEPTION_WRONG_REQUEST = "Некорректно составлен запрос";
    public static final String EXCEPTION_NOT_FOUND = "Объект не найден, 404";
    public static final String EXCEPTION_NOT_FOUND_USER = "Пользователь с id=%d не найден";
    public static final String EXCEPTION_EVENT_NOT_FOUND = "Событие с id=%d не найдено";
    public static final String EXCEPTION_COMMENT_NOT_PUBLISHED = "Комментарии доступны только к опубликованным мероприятиям";
    public static final String EXCEPTION_WRONG_DATE_RANGE = "Дата начала диапазона не может быть позже даты окончания";
    public static final String EXCEPTION_LIMIT_EXCEEDED = "Лимит запросов превышен, код ошибки 409";
    public static final String EXCEPTION_NOT_RESPOND_STATUS = "Статус запросов должен быть PENDING код ошибки 409";
    public static final String EXCEPTION_NOT_PUBLISHED = "Событие не найдено или недоступно";
    public static final String EXCEPTION_REQUEST_EXIST = "Запрос уже существует";
    public static final String EXCEPTION_ONLY_INITIATOR = "Только инициатор события может просматривать заявки на участие";
    public static final String EXCEPTION_CANT_UPDATE_PUBLISHED = "Нельзя изменить опубликованное событие";
    public static final String EXCEPTION_NOT_MEET_RULES = "Не соответствет требованиям.";
    public static final String EXCEPTION_REQUEST_INITIATOR_OWN = "Инициатор не может отправлять запросы на собственное мероприятие.";
    public static final String EXCEPTION_REQUEST_NOT_PUBLISHED = "Участие возможно только в опубликованном мероприятии.";
    public static final String EXCEPTION_REQUEST_LIMIT = "Достигнут лимит участников.";
    public static final String EXCEPTION_NOT_VALID = "Валидация не пройдена. Недопустимое значение.";
    public static final String EXCEPTION_COMMENT_NOT_FOUND = "Комментарий с id=%d не найден";
    public static final String EXCEPTION_ONLY_AUTHOR_CAN_EDIT = "Редактировать комментарий может только автор";
    public static final String EXCEPTION_COMMENT_FOR_OTHER_EVENT = "Этот комментарий относится к другому мероприятию";
    public static final String EXCEPTION_ONLY_AUTHOR_CAN_DELETE = "Удалить комментарий может только автор";
    public static final String NOT_FOUND_COMPLETION = "Подборка событий не найдена";
}
