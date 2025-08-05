package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.RestClientUtils;

@Configuration
@Slf4j
public class RestClientConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        log.info("=== DEBUG: RestTemplate created with HttpComponentsClientHttpRequestFactory ===");
        return new RestTemplate(factory);
    }

    @Bean
    public RestClientUtils restClientUtils(
            RestTemplate restTemplate,
            @Value("${shareit-server.url}") String serverUrl) {
        return new RestClientUtils(restTemplate, serverUrl);
    }
}
