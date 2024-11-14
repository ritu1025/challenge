package com.dws.challenge;

import com.dws.challenge.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class Challenge1ApplicationTests {

	@MockitoBean
	private NotificationService notificationService;

	@Test
	void contextLoads() {
	}

}
