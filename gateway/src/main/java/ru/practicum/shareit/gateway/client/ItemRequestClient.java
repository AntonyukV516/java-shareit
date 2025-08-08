package ru.practicum.shareit.gateway.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.dto.RequestDto;
import ru.practicum.shareit.gateway.dto.RequestItemDto;

import java.util.List;


@Slf4j
@Service
public class ItemRequestClient extends BaseClient {
    private static final String ITEM_REQUEST_PREFIX = "/requests";

    protected ItemRequestClient(RestTemplate restTemplate,
                                @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<RequestDto> addItemRequest(RequestItemDto requestItemDto, Long userId) {
        String url = serverUrl + ITEM_REQUEST_PREFIX;
        log.info("Попытка добавить запрос на элемент: {}, для пользователя с ID {}", requestItemDto, userId);
        return sendRequest(url, HttpMethod.POST, requestItemDto, RequestDto.class, userId);
    }

    public ResponseEntity<List<RequestDto>> getUserItemRequests(Long userId) {
        String url = serverUrl + ITEM_REQUEST_PREFIX;
        log.info("Запрос на получение запросов пользователя с ID {}", userId);
        return sendRequest(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<RequestDto>>() {}, userId);
    }

    public ResponseEntity<List<RequestDto>> getOtherUsersItemRequests(Long userId) {
        String url = serverUrl + ITEM_REQUEST_PREFIX + "/all";
        log.info("Запрос на получение запросов других пользователей для пользователя с ID {}", userId);
        return sendRequest(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<RequestDto>>() {}, userId);
    }

    public ResponseEntity<RequestDto> getItemRequestById(Long userId, Long requestId) {
        String url = serverUrl + ITEM_REQUEST_PREFIX + "/" + requestId;
        log.info("Запрос на получение запроса на элемент с ID {} для пользователя с ID {}", requestId, userId);
        return sendRequest(url, HttpMethod.GET, null, RequestDto.class, userId);
    }
}