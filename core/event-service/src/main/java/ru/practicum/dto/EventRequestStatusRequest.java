package main.java.ru.practicum.dto;

import main.java.ru.practicum.persistence.status.StatusRequest;

import java.util.List;

public record EventRequestStatusRequest(
        List<Long> requestIds,
        StatusRequest status
) {
}
