package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Message {

    public static final String GET_USER_ACTION = "Получено пользовательское действие {}";
    public static final String GET_EVENTS_SIMILARITY = "Получено соответствие событий {}";
    public static final String GET_INTERACTIONS_COUNT = "Пришел запрос {} о получении количества взаимодействий";
    public static final String GET_SIMILAR_EVENTS = "Пришел запрос {} о получении соответствий событий";
    public static final String GET_RECOMMENDATIONS = "Пришел запрос {} о получении рекомендаций";
}
