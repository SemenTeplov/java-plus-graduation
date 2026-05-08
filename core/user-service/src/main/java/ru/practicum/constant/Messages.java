package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {

    public static final String MESSAGE_NOT_FOUND = "Требуемый объект не был найден.";
    public static final String MESSAGE_INTERNAL_SERVER = "Внутренняя ошибка сервера: {}";
    public static final String MESSAGE_ERROR_VALIDATION = "Ошибка валидации: {}";
    public static final String MESSAGE_DELETE_USER = "DELETE /admin.users/{}";
    public static final String MESSAGE_GET_USERS = "GET /admin/users?ids={}&from={}&size={}";
    public static final String MESSAGE_REGISTER_USER = "POST /admin/users with request: {}";
    public static final String MESSAGE_NOT_VALID = "Значение не прошло валидацию: {}";
}
