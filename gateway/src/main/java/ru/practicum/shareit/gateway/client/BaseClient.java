package ru.practicum.shareit.gateway.client;

import org.springframework.http.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.Constants;


public abstract class BaseClient {
    protected final RestTemplate restTemplate;
    protected final String serverUrl;

    protected BaseClient(RestTemplate restTemplate,
                         String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    protected <T> ResponseEntity<T> sendRequest(String url, HttpMethod method,
                                                Object requestBody, Class<T> responseType,
                                                Long userId) {
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(url, method, requestEntity, responseType);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (RestClientException e) {
            throw new RuntimeException("Server error", e);
        }
    }

    protected <T> ResponseEntity<T> sendRequest(String url, HttpMethod method, Object requestBody, ParameterizedTypeReference<T> responseType, Long userId) {
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(url, method, requestEntity, responseType);
        } catch (RestClientException e) {
            throw e;
        }
    }

    protected <T> ResponseEntity<T> sendRequest(String url, HttpMethod method, Object requestBody, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(url, method, requestEntity, responseType);
        } catch (RestClientException e) {
            throw e;
        }
    }

    protected <T> ResponseEntity<T> sendRequest(String url, HttpMethod method, Object requestBody, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(url, method, requestEntity, responseType);
        } catch (RestClientException e) {
            throw e;
        }
    }

    private HttpHeaders createHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        if (userId != null) {
            headers.set(Constants.SHARER_USER_ID, String.valueOf(userId));
        }
        return headers;
    }
}