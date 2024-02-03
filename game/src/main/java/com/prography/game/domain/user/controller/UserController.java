package com.prography.game.domain.user.controller;

import com.prography.game.domain.user.request.UserInitRequest;
import com.prography.game.domain.user.request.UserListRequest;
import com.prography.game.domain.user.response.UserListResponse;
import com.prography.game.domain.user.service.UserInitService;
import com.prography.game.domain.user.service.UserService;
import com.prography.game.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserInitService userInitService;
    private final UserService userService;

    @Operation(summary = "초기화", description = "기존의 모든 정보를 삭제하고 새로운 회원 정보를 저장합니다.")
    @PostMapping("/init")
    public ApiResponse<Void> initializeUsers(@RequestBody @Valid UserInitRequest request) {

        userInitService.initializeUsers(request);
        return ApiResponse.success();
    }

    @Operation(summary = "유저 전체 조회", description = "모든 회원 정보를 id 기준 오름차순으로 정렬하여 리턴합니다.")
    @GetMapping("/user")
    public ApiResponse<UserListResponse> getUserList(@Parameter(required = true, description = "페이지(0부터 시작), 사이즈(1부터 시작)")
                                                     @Valid @ModelAttribute UserListRequest request) {
        UserListResponse userList = userService.getUserList(request);
        return ApiResponse.success(userList);
    }
}
