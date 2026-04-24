package java.exception;

public class StatsServerUnavailable extends RuntimeException {
    public StatsServerUnavailable(String id) {
        super( "Ошибка обнаружения адреса сервиса статистики с id: " + id);
    }
}
