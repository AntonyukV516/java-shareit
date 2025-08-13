package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.web.client.*;
import ru.practicum.shareit.gateway.client.UserClient;
import ru.practicum.shareit.gateway.dto.RequestUserDto;
import ru.practicum.shareit.gateway.dto.UserDto;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserClient userClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAdd() {
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@email.com");
        UserDto userDto = new UserDto(1L, "testName", "test@email.com");
        ResponseEntity<UserDto> responseEntity = ResponseEntity.ok(userDto);

        when(restTemplate.exchange(any(String.class),
                eq(HttpMethod.POST), any(HttpEntity.class), eq(UserDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<UserDto> result = userClient.add(requestUserDto);

        assertThat(result.getBody()).isEqualTo(userDto);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.POST),
                        any(HttpEntity.class), eq(UserDto.class));
    }

    @Test
    void testUpdateUser() {
        long userId = 1L;
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@email.com");
        UserDto userDto = new UserDto(userId, "testName", "test@email.com");
        ResponseEntity<UserDto> responseEntity = ResponseEntity.ok(userDto);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.PATCH),
                any(HttpEntity.class), eq(UserDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<UserDto> result = userClient.updateUser(userId, requestUserDto);

        assertThat(result.getBody()).isEqualTo(userDto);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.PATCH),
                        any(HttpEntity.class), eq(UserDto.class));
    }

    @Test
    void testDeleteUser() {
        long userId = 1L;

        when(restTemplate.execute(any(String.class), eq(HttpMethod.DELETE),
                any(RequestCallback.class), any(ResponseExtractor.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> result = userClient.deleteUser(userId);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        verify(restTemplate, times(1))
                .execute(any(String.class), eq(HttpMethod.DELETE),
                        any(RequestCallback.class), any(ResponseExtractor.class));
    }

    @Test
    void testGetUserById() {
        long userId = 1L;
        UserDto userDto = new UserDto(userId, "testName", "test@email.com");
        ResponseEntity<UserDto> responseEntity = ResponseEntity.ok(userDto);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(UserDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<UserDto> result = userClient.getUserById(userId);

        assertThat(result.getBody()).isEqualTo(userDto);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET),
                        any(HttpEntity.class), eq(UserDto.class));
    }

    @Test
    void testAdd_WhenServerReturnsError() {
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@email.com");
        String errorMessage = "Email already exists";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(UserDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, errorMessage));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> userClient.add(requestUserDto));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage()).contains(errorMessage);
    }

    @Test
    void testUpdateUser_WhenUserNotFound() {
        long userId = 999L;
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@email.com");
        String errorMessage = "User not found with id: " + userId;

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH),
                any(HttpEntity.class), eq(UserDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, errorMessage));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> userClient.updateUser(userId, requestUserDto));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).contains(errorMessage);
    }

    @Test
    void testDeleteUser_WhenUserNotFound() {
        long userId = 999L;
        String errorMessage = "User not found with id: " + userId;

        when(restTemplate.execute(any(String.class), eq(HttpMethod.DELETE),
                any(RequestCallback.class), any(ResponseExtractor.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, errorMessage));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> userClient.deleteUser(userId));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).contains(errorMessage);
    }

    @Test
    void testGetUserById_WhenUserNotFound() {
        long userId = 999L;
        String errorMessage = "User not found with id: " + userId;

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(UserDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, errorMessage));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> userClient.getUserById(userId));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).contains(errorMessage);
    }

    @Test
    void testAdd_WhenServerUnavailable() {
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@email.com");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(UserDto.class)))
                .thenThrow(new RestClientException("Connection refused"));

        RestClientException exception = assertThrows(RestClientException.class,
                () -> userClient.add(requestUserDto));

        assertThat(exception.getMessage()).contains("Connection refused");
    }

    @Test
    void testUpdateUser_WhenValidationFailed() {
        long userId = 1L;
        RequestUserDto requestUserDto = new RequestUserDto("", "invalid-email");
        String errorMessage = "Validation failed";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH),
                any(HttpEntity.class), eq(UserDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, errorMessage));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> userClient.updateUser(userId, requestUserDto));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage()).contains(errorMessage);
    }
    // Добавьте эти тесты в ваш класс UserClientTest

    @Test
    void testAdd_ShouldSetCorrectHeaders() {
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@email.com");
        UserDto userDto = new UserDto(1L, "testName", "test@email.com");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                argThat(entity -> {
                    assertThat(entity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                    return true;
                }),
                eq(UserDto.class)
        )).thenReturn(ResponseEntity.ok(userDto));

        userClient.add(requestUserDto);
    }

    @Test
    void testUpdateUser_WithNullBody() {
        long userId = 1L;
        UserDto userDto = new UserDto(userId, "testName", "test@email.com");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                argThat(entity -> entity.getBody() == null),
                eq(UserDto.class)
        )).thenReturn(ResponseEntity.ok(userDto));

        ResponseEntity<UserDto> result = userClient.updateUser(userId, null);

        assertThat(result.getBody()).isEqualTo(userDto);
    }

    @Test
    void testDeleteUser_VerifyHeadersCleared() throws IOException {
        long userId = 1L;
        ArgumentCaptor<RequestCallback> callbackCaptor = ArgumentCaptor.forClass(RequestCallback.class);

        when(restTemplate.execute(
                anyString(),
                eq(HttpMethod.DELETE),
                callbackCaptor.capture(),
                any(ResponseExtractor.class)
        )).thenReturn(ResponseEntity.ok().build());

        userClient.deleteUser(userId);

        MockClientHttpRequest request = new MockClientHttpRequest();
        callbackCaptor.getValue().doWithRequest(request);

        assertThat(request.getHeaders())
                .doesNotContainKeys("Transfer-Encoding", "Content-Length")
                .containsEntry("Connection", List.of("close"));
    }

    @Test
    void testGetUserById_WithEmptyResponseBody() {
        long userId = 1L;

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(UserDto.class)
        )).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<UserDto> result = userClient.getUserById(userId);

        assertThat(result.getBody()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testAdd_WhenServerReturnsInternalError() {
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@email.com");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(),
                eq(UserDto.class)
        )).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(HttpServerErrorException.class, () -> userClient.add(requestUserDto));
    }

    @Test
    void testUpdateUser_WhenServerUnavailable() {
        long userId = 1L;
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@email.com");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(),
                eq(UserDto.class)
        )).thenThrow(new ResourceAccessException("Connection timeout"));

        assertThrows(ResourceAccessException.class,
                () -> userClient.updateUser(userId, requestUserDto));
    }
}
