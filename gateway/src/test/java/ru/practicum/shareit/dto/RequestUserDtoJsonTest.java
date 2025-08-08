package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.gateway.dto.Marker;
import ru.practicum.shareit.gateway.dto.RequestUserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestUserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    public RequestUserDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testRequestUserDtoSerialization() throws Exception {
        RequestUserDto request = new RequestUserDto("testName", "test@email.com");
        String json = objectMapper.writeValueAsString(request);

        assertThat(json).contains("name");
        assertThat(json).contains("email");
    }

    @Test
    public void testRequestUserDtoDeserialization() throws Exception {
        String json = "{\"name\":\"testName\",\"email\":\"test@email.com\"}";
        RequestUserDto request = objectMapper.readValue(json, RequestUserDto.class);

        assertThat(request.getName()).isEqualTo("testName");
        assertThat(request.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    public void testRequestUserDtoValidationOnCreate() {
        RequestUserDto request = new RequestUserDto("", "invalid-email");

        Set<ConstraintViolation<RequestUserDto>> violations = validator.validate(
                request, Marker.OnCreate.class);

        assertThat(violations).hasSize(2);
    }

    @Test
    public void testRequestUserDtoValidationOnUpdate() {
        RequestUserDto request = new RequestUserDto("", "invalid-email");

        Set<ConstraintViolation<RequestUserDto>> violations = validator.validate(
                request, Marker.OnUpdate.class);

        assertThat(violations).hasSize(1);
    }
}