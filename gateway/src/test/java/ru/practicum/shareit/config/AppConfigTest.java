package ru.practicum.shareit.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void restTemplateBeanShouldBeCreated() {
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        assertNotNull(restTemplate);
    }

    @Test
    void restTemplateShouldUseHttpComponentsClient() {
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

        assertTrue(restTemplate.getRequestFactory() instanceof
                org.springframework.http.client.HttpComponentsClientHttpRequestFactory);
    }

    @Test
    void httpClientShouldBeConfiguredCorrectly() {
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        org.springframework.http.client.HttpComponentsClientHttpRequestFactory factory =
                (org.springframework.http.client.HttpComponentsClientHttpRequestFactory)
                        restTemplate.getRequestFactory();

        CloseableHttpClient httpClient = (CloseableHttpClient) factory.getHttpClient();
        assertNotNull(httpClient);
    }
}
