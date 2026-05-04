package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Exceptions {

    public static final String NOT_FOUND_COMPLETION = "Подборка событий не найдена";
    public static final String EXCEPTION_NOT_ILLEGAL_ARGUMENT = "Используется не допустимое значение";
    public static final String EXCEPTION_INTERNAL_SERVER = "Внутренняя ошибка сервера. Подождите несколько минут и попробуйте снова.";
}
