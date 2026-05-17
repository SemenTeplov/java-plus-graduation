package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Message {

    public static final String GET_LIST_OF_SENSORS = "Получен список пользователей для отправки {}";
    public static final String GET_LIST_OF_USER = "Получен список пользователей {}";
    public static final String EVENT_DON_T_EXIST = "Событие с id: {} отсутствует";
    public static final String USER_DON_T_EXIST = "Пользователь с id: {} отсутствует";
    public static final String USER_REDUCE_GRADLE = "Пользователь с id: {} уменьшил оценку";
    public static final String USER_INCREASE_GRADLE = "Пользователь с id: {} увеличил оценку";
    public static final String USER_DON_T_CHANGE_GRADLE = "Пользователь с id: {} неизменил оценку";
    public static final String DEFINITION_ORDER = "Определение порядка между {} и {} событиями";
    public static final String SUM = "Расчет суммы";
    public static final String MIN_ACCORDING = "Расчет минимального соответствия";
    public static final String SUM_WEIGHT = "Расчет суммы весов";
}
