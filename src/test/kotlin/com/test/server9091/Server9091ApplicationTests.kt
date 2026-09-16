package com.test.server9091

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest
class Server9091ApplicationTests {

	@Test
	fun contextLoads() {
	}

	@Test
	fun `returns a marked success response`() {
		val response = Server9091Controller().getJson("success")

		assertEquals(200, response.statusCode.value())
		assertEquals("mock-server-9091", response.body?.source)
		assertEquals(true, response.body?.success)
	}

	@Test
	fun `returns requested error status`() {
		val response = Server9091Controller().getJson("server-error")

		assertEquals(500, response.statusCode.value())
		assertEquals("mock-server-9091", response.body?.source)
		assertEquals(false, response.body?.success)
	}

}
