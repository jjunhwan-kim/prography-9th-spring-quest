package com.prography.game.domain.room.controller;

import com.prography.game.domain.room.request.*;
import com.prography.game.domain.room.response.RoomListResponse;
import com.prography.game.domain.room.response.RoomResponse;
import com.prography.game.domain.room.service.RoomService;
import com.prography.game.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/room")
    public ApiResponse<Void> createRoom(@RequestBody @Valid RoomRequest request) {

        roomService.createRoom(request);
        return ApiResponse.success();
    }

    @GetMapping("/room")
    public ApiResponse<RoomListResponse> getRoomList(@Valid RoomListRequest request) {

        RoomListResponse roomList = roomService.getRoomList(request);
        return ApiResponse.success(roomList);
    }

    @GetMapping("/room/{roomId}")
    public ApiResponse<RoomResponse> getRoom(@PathVariable Long roomId) {

        RoomResponse room = roomService.getRoom(roomId);
        return ApiResponse.success(room);
    }

    @PostMapping("/room/attention/{roomId}")
    public ApiResponse<Void> attendRoom(@PathVariable Long roomId,
                                        @RequestBody @Valid RoomAttentionRequest request) {

        roomService.attendRoom(roomId, request);
        return ApiResponse.success();
    }

    @PostMapping("/room/out/{roomId}")
    public ApiResponse<Void> leaveRoom(@PathVariable Long roomId,
                                       @RequestBody @Valid RoomOutRequest request) {

        roomService.leaveRoom(roomId, request);
        return ApiResponse.success();
    }

    @PutMapping("/room/start/{roomId}")
    public ApiResponse<Void> startGame(@PathVariable Long roomId,
                                       @RequestBody @Valid GameStartRequest request) {

        roomService.startGame(roomId, request);
        return ApiResponse.success();
    }

    @PutMapping("/team/{roomId}")
    public ApiResponse<Void> changeTeam(@PathVariable Long roomId,
                                        @RequestBody @Valid TeamChangeRequest request) {

        roomService.changeTeam(roomId, request);
        return ApiResponse.success();
    }
}
