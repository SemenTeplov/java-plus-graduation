package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Exceptions {

    public static final String EXCEPTION_CONFLICT_CATEGORY = "Категория с именем %s уже существует";
    public static final String EXCEPTION_REQUEST_EXIST = "Запрос уже существует";
    public static final String EXCEPTION_NOT_ILLEGAL_ARGUMENT = "Используется не допустимое значение";
    public static final String EXCEPTION_INTERNAL_SERVER = "Внутренняя ошибка сервера. Подождите несколько минут и попробуйте снова.";
    public static final String EXCEPTION_WRONG_REQUEST = "Некорректно составлен запрос";
    public static final String EXCEPTION_NOT_FOUND = "Объект не найден, 404";
    public static final String EXCEPTION_NOT_CATEGORY_VALIDATION = "Имя категории обязательно для заполнения";
    public static final String EXCEPTION_NOT_EMPTY_CATEGORY_VALIDATION = "Имя категории не может быть пустым или состоять только из пробелов";
    public static final String EXCEPTION_LENGTH_MORE_ONE_CATEGORY_VALIDATION = "Имя категории должно содержать хотя бы 1 символ";
    public static final String EXCEPTION_LENGTH_LESS_FIFTY_CATEGORY_VALIDATION = "Имя категории не может превышать 50 символов";
    public static final String EXCEPTION_CANT_DELETE_CATEGORY = "Невозможно удалить категорию, так как с ней связаны события";
    public static final String EXCEPTION_NOT_VALID = "Валидация не пройдена. Недопустимое значение.";
}
