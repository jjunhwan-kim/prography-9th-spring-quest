package com.prography.game.domain.user.controller;

import com.prography.game.domain.user.request.UserInitRequest;
import com.prography.game.domain.user.request.UserListRequest;
import com.prography.game.domain.user.response.UserListResponse;
import com.prography.game.domain.user.service.UserInitService;
import com.prography.game.domain.user.service.UserService;
import com.prography.game.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserInitService userInitService;
    private final UserService userService;

    @PostMapping("/init")
    public ApiResponse<Void> initializeUsers(@RequestBody @Valid UserInitRequest request) {

        userInitService.initializeUsers(request);
        return ApiResponse.success();
    }

    @GetMapping("/user")
    public ApiResponse<UserListResponse> getUserList(@Valid UserListRequest request) {

        UserListResponse userList = userService.getUserList(request);
        return ApiResponse.success(userList);
    }
}
