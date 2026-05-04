package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {

    public static final String MESSAGE_ADD_CATEGORIES = "POST /admin/categories with request: {}";
    public static final String MESSAGE_INTERNAL_SERVER = "Внутренняя ошибка сервера: {}";
    public static final String MESSAGE_ERROR_VALIDATION = "Ошибка валидации: {}";
    public static final String MESSAGE_CATEGORY_NOT_FOUND = "Категория с id=%d не найдена";
    public static final String MESSAGE_NOT_FOUND = "Требуемый объект не был найден.";
    public static final String MESSAGE_DELETE_CATEGORIES = "DELETE /admin/categories/{}";
    public static final String MESSAGE_GET_CATEGORIES = "GET /categories";
    public static final String MESSAGE_GET_CATEGORY = "GET /categories/{}";
    public static final String MESSAGE_UPDATE_CATEGORY = "PATCH /admin/categories/{} with request: {}";
}
