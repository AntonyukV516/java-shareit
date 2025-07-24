package ru.practicum.shareit;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.RestClientUtils;

@TestConfiguration
public class TestConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestClientUtils restClientUtils() {
        return new RestClientUtils(restTemplate(), "http://testserver");
    }
}