package com.example.microserviciob.microserviciob;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.microserviciob.microserviciob.client.ClientFeignMSA;

@SpringBootTest
class MicroserviciobApplicationTests {

	@MockitoBean
	private ClientFeignMSA clientFeignMSA;

	@Test
	void contextLoads() {
	}

}
