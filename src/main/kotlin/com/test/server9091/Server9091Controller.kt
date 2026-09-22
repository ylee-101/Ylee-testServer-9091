package com.test.server9091

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mock")
class Server9091Controller {
    @GetMapping("/json")
    fun getJson(
        @RequestParam(defaultValue = "success") scenario: String,
    ): ResponseEntity<MockJsonResponse> = when (scenario.lowercase()) {
        "success" -> response(HttpStatus.OK, scenario, true, "Mock request processed successfully")
        "bad-request" -> response(HttpStatus.BAD_REQUEST, scenario, false, "Invalid mock request")
        "unauthorized" -> response(HttpStatus.UNAUTHORIZED, scenario, false, "Mock authentication failed")
        "server-error" -> response(HttpStatus.INTERNAL_SERVER_ERROR, scenario, false, "Mock server error")
        "slow-response" -> {
            Thread.sleep(SLOW_RESPONSE_DELAY_MILLIS)
            response(HttpStatus.OK, scenario, true, "Mock response delayed by 30 seconds")
        }
        else -> response(HttpStatus.BAD_REQUEST, scenario, false, "Unknown scenario: $scenario")
    }

    private fun response(
        status: HttpStatus,
        scenario: String,
        success: Boolean,
        message: String,
    ) = ResponseEntity.status(status).body(
        MockJsonResponse(
            source = "mock-server-9091",
            scenario = scenario,
            success = success,
            message = message,
        ),
    )

    companion object {
        const val SLOW_RESPONSE_DELAY_MILLIS = 30_000L
    }
}

data class MockJsonResponse(
    val source: String,
    val scenario: String,
    val success: Boolean,
    val message: String,
)
