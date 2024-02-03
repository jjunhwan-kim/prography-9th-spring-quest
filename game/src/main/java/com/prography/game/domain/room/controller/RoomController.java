package com.prography.game.domain.room.controller;

import com.prography.game.domain.room.request.*;
import com.prography.game.domain.room.response.RoomListResponse;
import com.prography.game.domain.room.response.RoomResponse;
import com.prography.game.domain.room.service.RoomService;
import com.prography.game.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "방")
@RequiredArgsConstructor
@RestController
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "방 생성", description = "방을 생성합니다.")
    @PostMapping("/room")
    public ApiResponse<Void> createRoom(@RequestBody @Valid RoomRequest request) {

        roomService.createRoom(request);
        return ApiResponse.success();
    }

    @Operation(summary = "방 전체 조회", description = "모든 방 정보를 id 기준 오름차순으로 정렬하여 리턴합니다.")
    @GetMapping("/room")
    public ApiResponse<RoomListResponse> getRoomList(@Valid RoomListRequest request) {

        RoomListResponse roomList = roomService.getRoomList(request);
        return ApiResponse.success(roomList);
    }

    @Operation(summary = "방 상세 조회", description = "방 하나에 대한 정보를 리턴합니다.")
    @GetMapping("/room/{roomId}")
    public ApiResponse<RoomResponse> getRoom(@Parameter(required = true, description = "방 ID")
                                             @PathVariable Long roomId) {

        RoomResponse room = roomService.getRoom(roomId);
        return ApiResponse.success(room);
    }

    @Operation(summary = "방 참가", description = "방에 참가합니다.")
    @PostMapping("/room/attention/{roomId}")
    public ApiResponse<Void> attendRoom(@Parameter(required = true, description = "방 ID")
                                        @PathVariable Long roomId,
                                        @RequestBody @Valid RoomAttentionRequest request) {

        roomService.attendRoom(roomId, request);
        return ApiResponse.success();
    }

    @Operation(summary = "방 나가기", description = "방에서 나갑니다. 호스트가 방을 나가면 모든 사람이 나가게 됩니다.")
    @PostMapping("/room/out/{roomId}")
    public ApiResponse<Void> leaveRoom(@Parameter(required = true, description = "방 ID")
                                       @PathVariable Long roomId,
                                       @RequestBody @Valid RoomOutRequest request) {

        roomService.leaveRoom(roomId, request);
        return ApiResponse.success();
    }

    @Operation(summary = "게임시작", description = "게임을 시작합니다. 호스트 유저만 게임을 시작할 수 있습니다.")
    @PutMapping("/room/start/{roomId}")
    public ApiResponse<Void> startGame(@Parameter(required = true, description = "방 ID")
                                       @PathVariable Long roomId,
                                       @RequestBody @Valid GameStartRequest request) {

        roomService.startGame(roomId, request);
        return ApiResponse.success();
    }

    @Operation(summary = "팀 변경", description = "팀을 변경합니다. 현재 속한 팀 기준 반대 팀으로 변경됩니다.")
    @PutMapping("/team/{roomId}")
    public ApiResponse<Void> changeTeam(@Parameter(required = true, description = "방 ID")
                                        @PathVariable Long roomId,
                                        @RequestBody @Valid TeamChangeRequest request) {
        roomService.changeTeam(roomId, request);
        return ApiResponse.success();
    }
}
