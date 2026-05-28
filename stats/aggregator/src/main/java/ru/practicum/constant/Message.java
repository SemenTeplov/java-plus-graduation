package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Message {

    public static final String SEND_LIST = "На отправку уходит следующий списсок {}";
    public static final String GET_USER_ACTION_FROM_KAFKA = "Из топика {} прибыло пользовательское действие {}";
    public static final String GET_LIST_OF_USER = "Получен список пользователей {}";
    public static final String EVENT_DON_T_EXIST = "Событие с id: {} отсутствует";
    public static final String TAKE_EVENTS_SIMILARITY = "Передано одно событие {}, второе событие {} и их соответствие {}";
    public static final String DEFINITION_ORDER = "Определение порядка между {} и {} событиями";
    public static final String SUM_RESULT = "Расчитано минимальное соответствие {} у событий {} {}";
    public static final String MIN_ACCORDING = "Расчитано минимальное соответствие {}";
    public static final String SUM_WEIGHT = "Расчет суммы весов {}";
}
