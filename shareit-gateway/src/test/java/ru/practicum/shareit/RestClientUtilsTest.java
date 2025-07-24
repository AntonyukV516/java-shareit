package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.gateway.RestClientUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestClientUtilsTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestClientUtils restClientUtils;

    private final String serverUrl = "http://localhost:8080";
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        restClientUtils = new RestClientUtils(restTemplate, serverUrl);
    }

    @Test
    void post_ShouldReturnResponseBody_WhenSuccessful() {
        String path = "/test";
        String requestBody = "request";
        String responseBody = "response";

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                any()
        )).thenReturn(ResponseEntity.ok(responseBody));

        String result = restClientUtils.post(path, requestBody, userId, String.class);

        assertEquals(responseBody, result);
        verify(restTemplate).postForEntity(
                eq(serverUrl + path),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void patch_ShouldReturnResponseBody_WhenSuccessful() {
        String path = "/test/{id}";
        String requestBody = "request";
        String responseBody = "response";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(serverUrl + path),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(String.class),
                any(Object.class))
        ).thenReturn(responseEntity);

        String result = restClientUtils.patch(path, requestBody, userId, String.class, 1L);

        assertEquals(responseBody, result);
    }

    @Test
    void get_ShouldReturnResponseBody_WhenSuccessful() {
        String path = "/test/{id}";
        String responseBody = "response";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(serverUrl + path),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class),
                any(Object.class))
        ).thenReturn(responseEntity);

        String result = restClientUtils.get(path, userId, String.class, 1L);

        assertEquals(responseBody, result);
    }

    @Test
    void delete_ShouldReturnResponseBody_WhenSuccessful() {
        String path = "/test/{id}";
        String responseBody = "response";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(serverUrl + path),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(String.class),
                any(Object.class))
        ).thenReturn(responseEntity);

        String result = restClientUtils.delete(path, userId, String.class, 1L);

        assertEquals(responseBody, result);
    }

    @Test
    void validateResponse_ShouldThrowResponseStatusException_WhenNot2xx() {
        String errorBody = "Error message";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> restClientUtils.validateResponse(responseEntity));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(errorBody, exception.getReason());
    }

    @Test
    void createRequestEntity_ShouldSetCorrectHeaders() {
        String body = "test body";

        HttpEntity<String> entity = restClientUtils.createRequestEntity(body, userId);

        assertNotNull(entity.getHeaders());
        assertEquals(MediaType.APPLICATION_JSON, entity.getHeaders().getContentType());
        assertEquals(List.of(MediaType.APPLICATION_JSON), entity.getHeaders().getAccept());
        assertEquals(String.valueOf(userId), entity.getHeaders().getFirst(Constants.SHARER_USER_ID));
        assertEquals(body, entity.getBody());
    }

    @Test
    void createRequestEntity_ShouldHandleNullBody() {
        // Act
        HttpEntity<Void> entity = restClientUtils.createRequestEntity(null, userId);

        // Assert
        assertNull(entity.getBody());
        assertNotNull(entity.getHeaders());
        assertEquals(String.valueOf(userId), entity.getHeaders().getFirst(Constants.SHARER_USER_ID));
    }

    @Test
    void postWithUriVariables_ShouldReturnResponseBody() {
        String path = "/test/{id}";
        String requestBody = "request";
        String responseBody = "response";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.postForEntity(
                eq(serverUrl + path),
                any(HttpEntity.class),
                eq(String.class),
                eq(1L))
        ).thenReturn(responseEntity);

        String result = restClientUtils.post(path, requestBody, userId, String.class, 1L);

        assertEquals(responseBody, result);
    }


    @Test
    void get_ShouldPassCorrectUriVariables() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("response", HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(String.class),
                eq(1L), eq("param")))
                .thenReturn(responseEntity);

        String result = restClientUtils.get("/path/{id}/{param}", userId, String.class, 1L, "param");

        assertNotNull(result);
    }
}