package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = GatewayApp.class)
@ActiveProfiles("test")
class GatewayTests {

	@Test
	void contextLoads() {
	}

}
