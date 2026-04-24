package main.client;

import dto.EndpointHitDto;
import dto.ViewStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsClient {

    @Autowired
    private RestClient client;

    public void saveHit(EndpointHitDto hitDto) {
        client.post()
                .uri("/hit")
                .body(hitDto)
                .retrieve()
                .toBodilessEntity();
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        UriBuilder builder = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        if (uris != null) {
            builder.queryParam("uris", uris);
        }

        return client.get()
                .uri(builder.toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<List<ViewStats>>() {});
    }
}
