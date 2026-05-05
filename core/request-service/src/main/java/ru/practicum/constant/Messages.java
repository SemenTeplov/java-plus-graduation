package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {

    public static final String MESSAGE_INTERNAL_SERVER = "Внутренняя ошибка сервера: {}";
    public static final String MESSAGE_ERROR_VALIDATION = "Ошибка валидации: {}";
    public static final String MESSAGE_NOT_FOUND = "Требуемый объект не был найден.";
    public static final String MESSAGE_ADDED_REQUEST = "Заявка создана успешно. ID заявки: {}, статус: {}";
    public static final String MESSAGE_CANCEL_REQUEST = "Заявка {} успешно отменена пользователем {}";
    public static final String MESSAGE_GET_REQUESTS = "Найдены заявки для пользователя {}";
    public static final String METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION = "Ошибка валидации";
    public static final String MESSAGE_NOT_VALID = "Значение не прошло валидацию: {}";
}
