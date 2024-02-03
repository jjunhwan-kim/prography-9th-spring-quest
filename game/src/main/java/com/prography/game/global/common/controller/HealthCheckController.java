package com.prography.game.global.common.controller;

import com.prography.game.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공통")
@RestController
public class HealthCheckController {

    @Operation(summary = "헬스체크", description = "서버의 상태를 체크하는 API입니다.")
    @GetMapping("/health")
    public ApiResponse<Void> getHealth() {
        return ApiResponse.success();
    }
}
