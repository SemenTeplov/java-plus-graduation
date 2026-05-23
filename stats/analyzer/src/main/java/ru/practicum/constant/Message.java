package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Message {

    public static final String GET_USER_ACTION = "Получено пользовательское действие {}";
    public static final String TAKE_EVENT_ID = "Получен id события {}";
    public static final String TAKE_EVENT_SCORE = "Получено соответствие события {}";
    public static final String TAKE_NEW_SCORE = "Из суммы весов {} и коэффицента выведено соответствие {}";
    public static final String SCORE_ACCORDING = "Сравнение разности нового соответствия {} со старым {}";
    public static final String LIST_EMPTY = "Список пуст";
    public static final String SAVE_USER_ACTION = "Пользовательское действие {} сохранено";
    public static final String GET_EVENTS_SIMILARITY = "Получено соответствие событий {}";
    public static final String SAVE_EVENTS_SIMILARITY = "Событие {} сохранено";
    public static final String GET_INTERACTIONS_COUNT = "Пришел запрос {} о получении количества взаимодействий";
    public static final String GET_INTERACTIONS_COUNT_VALUE = "событие %s";
    public static final String TAKE_USER_ACTION = "Из репозитория было получено {} пользовательское действие";
    public static final String TAKE_INTERACTION_COUNT = "Отпровляется ответом количество взаимодействий {}";
    public static final String TAKE_INTERACTION_COUNT_VALUE = "событие %s, взаимодействие %f";
    public static final String GET_SIMILAR_EVENTS = "Пришел запрос событие: {}, пользователь: {}, ожидаемый результат: {} о получении соответствий событий";
    public static final String TAKE_EVENT_SIMILARITY = "Получено и отфильтровано соответствие событий {}";
    public static final String GET_RECOMMENDATIONS = "Пришел запрос {} о получении рекомендаций";
    public static final String TAKE_RECOMMENDATIONS = "Получена рекомендация для схожих событий";
    public static final String TAKE_RECOMMENDATIONS_FOR_USER = "Получена рекомендация для пользователя";
}
