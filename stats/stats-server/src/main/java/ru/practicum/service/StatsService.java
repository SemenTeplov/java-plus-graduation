package main.java.ru.practicum.service;

import main.dto.EndpointHitDto;
import main.dto.ViewStats;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface StatsService {
    void saveHit(EndpointHitDto hitDto);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
