package ru.practicum.shareit.gateway;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.Constants;

import java.util.List;

public class RestClientUtils {
    private final RestTemplate restTemplate;
    private final String serverUrl;

    public RestClientUtils(RestTemplate restTemplate, String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    public <T, R> R post(String path, T requestBody, Long userId, Class<R> responseType) {
        try {
            HttpEntity<T> requestEntity = createRequestEntity(requestBody, userId);
            ResponseEntity<R> response = restTemplate.postForEntity(
                    serverUrl + path,
                    requestEntity,
                    responseType);
            return validateResponse(response);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getResponseBodyAsString()
            );
        }
    }


    public <T> HttpEntity<T> createRequestEntity(T body, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set(Constants.SHARER_USER_ID, String.valueOf(userId));
        return new HttpEntity<>(body, headers);
    }


    public <T> T validateResponse(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        throw new ResponseStatusException(
                response.getStatusCode(),
                response.getBody() != null ? response.getBody().toString() : "No error message"
        );
    }


    public <T, R> R patch(String path, T requestBody, Long userId, Class<R> responseType, Object... uriVariables) {
        HttpEntity<T> requestEntity = createRequestEntity(requestBody, userId);
        ResponseEntity<R> response = restTemplate.exchange(
                serverUrl + path,
                HttpMethod.PATCH,
                requestEntity,
                responseType,
                uriVariables);
        return validateResponse(response);
    }


    public <T> T get(String path, Long userId, Class<T> responseType, Object... uriVariables) {
        HttpEntity<Void> requestEntity = createRequestEntity(null, userId);
        ResponseEntity<T> response = restTemplate.exchange(
                serverUrl + path,
                HttpMethod.GET,
                requestEntity,
                responseType,
                uriVariables);
        return validateResponse(response);
    }

    public <T, R> R post(String path, T requestBody, Long userId, Class<R> responseType, Object... uriVariables) {
        HttpEntity<T> requestEntity = createRequestEntity(requestBody, userId);
        ResponseEntity<R> response = restTemplate.postForEntity(
                serverUrl + path,
                requestEntity,
                responseType,
                uriVariables);
        return validateResponse(response);
    }

    public <R> R delete(String path, Long userId, Class<R> responseType, Object... uriVariables) {
        HttpEntity<Void> requestEntity = createRequestEntity(null, userId);
        ResponseEntity<R> response = restTemplate.exchange(
                serverUrl + path,
                HttpMethod.DELETE,
                requestEntity,
                responseType,
                uriVariables
        );
        return validateResponse(response);
    }
}