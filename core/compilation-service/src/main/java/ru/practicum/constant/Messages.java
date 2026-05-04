package main.java.ru.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {

    public static final String GET_COMPILATIONS = "Пришел запрос на получение подборки событий";
    public static final String GET_COMPILATION = "Пришел запрос на получение подборки события по id {}";
    public static final String NOT_FOUND_COMPLETION = "Подборка событий по id не найдено";
    public static final String MESSAGE_INTERNAL_SERVER = "Внутренняя ошибка сервера: {}";
    public static final String SAVE_COMPILATION = "Пришел запрос на сохранение подборки события {}";
    public static final String DELETE_COMPILATION = "Пришел запрос на удаление подборки события {}";
    public static final String UPDATE_COMPILATION = "Пришел запрос на обновление подборки события {}";
}
