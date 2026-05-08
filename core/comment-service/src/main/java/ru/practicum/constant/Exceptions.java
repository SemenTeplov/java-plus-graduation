package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Exceptions {

    public static final String EXCEPTION_COMMENT_IS_EMPTY = "Текст комментария не может быть пустым";
    public static final String EXCEPTION_NOT_ILLEGAL_ARGUMENT = "Используется не допустимое значение";
    public static final String EXCEPTION_INTERNAL_SERVER = "Внутренняя ошибка сервера. Подождите несколько минут и попробуйте снова.";
    public static final String EXCEPTION_WRONG_REQUEST = "Некорректно составлен запрос";
    public static final String EXCEPTION_COMMENT_NOT_FOUND = "Комментарий с id=%d не найден";
    public static final String EXCEPTION_NOT_FOUND = "Объект не найден, 404";
    public static final String EXCEPTION_ONLY_AUTHOR_CAN_EDIT = "Редактировать комментарий может только автор";
    public static final String EXCEPTION_COMMENT_FOR_OTHER_EVENT = "Этот комментарий относится к другому мероприятию";
    public static final String EXCEPTION_ONLY_AUTHOR_CAN_DELETE = "Удалить комментарий может только автор";
    public static final String EXCEPTION_NOT_VALID = "Валидация не пройдена. Недопустимое значение.";
}
