package ru.practicum.shareit.gateway.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.dto.RequestUserDto;
import ru.practicum.shareit.gateway.dto.UserDto;

@Slf4j
@Service
public class UserClient extends BaseClient {
    private static final String USER_PREFIX = "/users";

    protected UserClient(RestTemplate restTemplate,
                         @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<UserDto> add(RequestUserDto requestUserDto) {
        String url = serverUrl + USER_PREFIX;
        log.info("Попытка добавить User");
        return sendRequest(url, HttpMethod.POST, requestUserDto, UserDto.class);
    }

    public ResponseEntity<UserDto> updateUser(long userId, RequestUserDto requestUserDto) {
        log.info("Попытка обновить User");
        String url = serverUrl + USER_PREFIX + "/" + userId;
        return sendRequest(url, HttpMethod.PATCH, requestUserDto, UserDto.class);
    }

    public ResponseEntity<?> deleteUser(long userId) {
        String url = serverUrl + USER_PREFIX + "/" + userId;
        return sendRequest(url, HttpMethod.DELETE, null, Void.class);
    }

    public ResponseEntity<UserDto> getUserById(long userId) {
        String url = serverUrl + USER_PREFIX + "/" + userId;
        return sendRequest(url, HttpMethod.GET, null, UserDto.class);
    }
}