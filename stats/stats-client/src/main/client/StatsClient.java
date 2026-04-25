package main.client;

import dto.EndpointHitDto;
import dto.ViewStats;

import main.exception.StatsServerUnavailable;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsClient {

    private final DiscoveryClient discoveryClient;

    public StatsClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public void saveHit(EndpointHitDto hitDto) {
        restClient().post()
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

        return restClient().get()
                .uri(builder.toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<List<ViewStats>>() {});
    }

    private RestClient restClient() {

        ServiceInstance serviceInstance = getRetryTemplate().execute(cxt -> getInstance());

        return RestClient.builder()
                .baseUrl("http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort())
                .build();
    }

    private RetryTemplate getRetryTemplate() {

        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient.getInstances("main-service").getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailable("main-service");
        }
    }
}
