package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {

    public static final String MESSAGE_ADD_COMMENT = "Добавление комментария пользователя {} к событию {}";
    public static final String MESSAGE_INTERNAL_SERVER = "Внутренняя ошибка сервера: {}";
    public static final String MESSAGE_ERROR_VALIDATION = "Ошибка валидации: {}";
    public static final String MESSAGE_UPDATE_COMMENT = "Обновление комментария пользователя {} для события {}, комментарий {}";
    public static final String MESSAGE_NOT_FOUND = "Требуемый объект не был найден.";
    public static final String MESSAGE_GET_COMMENTS_BY_AUTHOR = "Получение комментариев от автора {}";
    public static final String MESSAGE_GET_COMMENTS_BY_EVENT = "Получение комментариев к событию {}";
    public static final String MESSAGE_GET_COMMENT_BY_ID = "Получение комментария по идентификатору {}";
    public static final String MESSAGE_DELETE_COMMENT = "Удаление комментария {} пользователем {}";
    public static final String MESSAGE_DELETE_COMMENT_BY_ADMIN = "Удаление комментария {} администратором";
}
