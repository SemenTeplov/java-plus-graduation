package main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClient;

import main.client.StatsClient;
import main.exception.StatsServerUnavailable;

@AutoConfiguration
public class StatsClientConfig {

    private final Environment environment;

    private final DiscoveryClient discoveryClient;

    @Autowired
    public StatsClientConfig(Environment environment, DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        this.environment = environment;
    }

    @Bean
    public RestClient restClient() {

        ServiceInstance serviceInstance = getRetryTemplate().execute( cxt -> getInstance());

        return RestClient.builder()
                .baseUrl("http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort())
                .build();
    }

    @Bean
    public StatsClient statsClient(RestClient client) {
        return new StatsClient(client);
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
            return discoveryClient.getInstances(environment.getProperty("stats.client.nameService")).getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailable(environment.getProperty("stats.client.nameService"));
        }
    }
}
