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
    public static final String MESSAGE_COUNT = "GET /request/{}/{} response Long";
    public static final String MESSAGE_GET_ALL = "GET /request response List<ParticipationRequestDto>";
    public static final String MESSAGE_GET_REQUEST = "GET /request/client response List<ParticipationRequestDto>";
    public static final String MESSAGE_ADD_REQUEST = "POST /users/requests with body {} response List<ParticipationRequestDto>";
    public static final String MESSAGE_GET_REQUEST_BY_EVENT = "GET /request/client/{} response List<ParticipationRequestDto>";
}
