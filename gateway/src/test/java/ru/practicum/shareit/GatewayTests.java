package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.gateway.RestClientUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GatewayTests {

	@MockBean
	private RestClientUtils restClientUtils;

	@Test
	void contextLoads() {
		assertNotNull(restClientUtils);
	}

}
