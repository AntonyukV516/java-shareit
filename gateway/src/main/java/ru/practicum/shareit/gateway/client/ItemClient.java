package ru.practicum.shareit.gateway.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.dto.CommentDto;
import ru.practicum.shareit.gateway.dto.ItemBodyDto;
import ru.practicum.shareit.gateway.dto.ItemDto;
import ru.practicum.shareit.gateway.dto.RequestCommentDto;

import java.util.Collection;
import java.util.List;


@Slf4j
@Service
public class ItemClient extends BaseClient {
    private static final String ITEM_PREFIX = "/items";

    protected ItemClient(RestTemplate restTemplate,
                         @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<ItemDto> add(ItemBodyDto itemBodyDto, long userId) {
        String url = serverUrl + ITEM_PREFIX;
        return sendRequest(url, HttpMethod.POST, itemBodyDto, ItemDto.class, userId);
    }

    public ResponseEntity<ItemDto> update(long itemsId, ItemBodyDto itemBodyDto, Long userId) {
        String url = serverUrl + ITEM_PREFIX + "/" + itemsId;
        return sendRequest(url, HttpMethod.PATCH, itemBodyDto, ItemDto.class, userId);
    }

    public ResponseEntity<ItemDto> getById(long itemsId) {
        String url = serverUrl + ITEM_PREFIX + "/" + itemsId;
        return sendRequest(url, HttpMethod.GET, null, ItemDto.class, null);
    }

    public ResponseEntity<Collection<ItemDto>> getItemsByOwner(Long userId) {
        String url = serverUrl + ITEM_PREFIX + "?ownerId=" + userId;
        return sendRequest(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<Collection<ItemDto>>() {}, userId);
    }

    public ResponseEntity<List<ItemDto>> search(String text, Long userId) {
        String url = serverUrl + ITEM_PREFIX + "/search?text=" + text;
        return sendRequest(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ItemDto>>() {}, userId);
    }

    public ResponseEntity<CommentDto> addComment(Long itemId, RequestCommentDto requestCommentDto, Long userId) {
        String url = serverUrl + ITEM_PREFIX + "/" + itemId + "/comment";
        return sendRequest(url, HttpMethod.POST, requestCommentDto, CommentDto.class, userId);
    }

}