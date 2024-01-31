package com.prography.game.global.common.controller;

import com.prography.game.global.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ApiResponse<Void> getHealth() {
        return ApiResponse.success();
    }
}
