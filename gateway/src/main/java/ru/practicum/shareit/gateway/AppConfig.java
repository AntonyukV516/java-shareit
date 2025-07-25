package ru.practicum.shareit.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    @Bean
    public RestClientUtils restClientUtils(
            RestTemplate restTemplate,
            @Value("${shareit-server.url}") String serverUrl) {
        return new RestClientUtils(restTemplate, serverUrl);
    }
}
