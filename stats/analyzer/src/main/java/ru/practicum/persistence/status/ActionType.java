package main.java.ru.practicum.persistence.status;

public enum ActionType {

    VIEW(0.4),
    REGISTER(0.8),
    LIKE(1.0);

    private final double value;

    ActionType(double value) {
        this.value = value;
    }

    public static ActionType fromValue(double value) {
        for (ActionType status : ActionType.values()) {
            if (status.value == value) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown status value: " + value);
    }

    public static Double getValue(String value) {
        for (ActionType status : ActionType.values()) {
            if (status.name().equals(value)) {
                return status.value;
            }
        }

        throw new IllegalArgumentException("Unknown status value: " + value);
    }
}
