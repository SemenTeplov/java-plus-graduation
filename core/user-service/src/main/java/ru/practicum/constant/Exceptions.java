package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Exceptions {

    public static final String EXCEPTION_REQUEST_EXIST = "Запрос уже существует";
    public static final String EXCEPTION_NOT_ILLEGAL_ARGUMENT = "Используется не допустимое значение";
    public static final String EXCEPTION_NOT_FOUND = "Объект не найден, 404";
    public static final String EXCEPTION_INTERNAL_SERVER = "Внутренняя ошибка сервера. Подождите несколько минут и попробуйте снова.";
    public static final String EXCEPTION_WRONG_REQUEST = "Некорректно составлен запрос";
    public static final String EXCEPTION_CONFLICT_EMAIL = "Пользователь с email %s уже существует";
    public static final String EXCEPTION_NOT_FOUND_USER = "Пользователь с id=%d не найден";
}
