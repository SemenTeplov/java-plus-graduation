package main.dto;

import main.status.StatusRequest;

import java.util.List;

public record EventRequestStatusRequest(
        List<Long> requestIds,
        StatusRequest status
) {
}
