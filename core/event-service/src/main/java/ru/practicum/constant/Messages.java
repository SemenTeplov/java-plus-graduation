package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {

    public static final String MESSAGE_ADD_EVENT = "POST /users/{}/events users with request: {}";
    public static final String MESSAGE_DATE_MISMATCH = "Дата не соответствует требованиям";
    public static final String MESSAGE_INTERNAL_SERVER = "Внутренняя ошибка сервера: {}";
    public static final String MESSAGE_ERROR_VALIDATION = "Ошибка валидации: {}";
    public static final String MESSAGE_NOT_FOUND = "Требуемый объект не был найден.";
    public static final String MESSAGE_CHANGE_STATUS = "Поступил запрос на изменения статуса";
    public static final String MESSAGE_LIMIT_EXCEEDED = "Лимит запросов превышен";
    public static final String MESSAGE_NOT_RESPOND_STATUS = "Статус запросов должен быть PENDING";
    public static final String MESSAGE_GET_EVENT_BY_ID = "Поступил запрос на получение события по id: {}";
    public static final String MESSAGE_GET_PARTICIPANTS =
            "Поступил запрос на получение информации о запросах от пользователя: {} для события: {}";
    public static final String MESSAGE_GET_EVENTS_BY_USER_ID_AND_EVENT_ID =
            "Поступил запрос на получение информации о событиии: {} от пользователя: {}";
    public static final String MESSAGE_GET_EVENTS = "Поступил запрос на получение события";
    public static final String MESSAGE_GET_EVENTS_FOR_ADMIN = "Поступил запрос на получение события администратором";
    public static final String MESSAGE_GET_EVENTS_FOR_USER = "Поступил запрос на получение события пользователем";
    public static final String MESSAGE_UPDATE_EVENT = "Поступил запрос на обновление события";
}
