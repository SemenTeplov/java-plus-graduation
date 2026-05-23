package main.java.ru.practicum.status;

public enum EventStatus {
    VIEW(0.4),
    REGISTER(0.8),
    LIKE(1.0);

    private final double value;

    EventStatus(double value) {
        this.value = value;
    }

    public static EventStatus fromValue(double value) {

        for (EventStatus status : EventStatus.values()) {

            if (status.value == value) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown status value: " + value);
    }

    public static double getValue(String value) {

        for (EventStatus status : EventStatus.values()) {

            if (status.name().equals(value)) {
                return status.value;
            }
        }

        throw new IllegalArgumentException("Unknown status value: " + value);
    }
}
