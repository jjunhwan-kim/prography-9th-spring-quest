package com.prography.game.domain.room.controller;

import com.prography.game.domain.ControllerTest;
import com.prography.game.domain.room.entity.Room;
import com.prography.game.domain.room.entity.RoomStatus;
import com.prography.game.domain.room.entity.RoomType;
import com.prography.game.domain.room.request.*;
import com.prography.game.domain.room.response.RoomListResponse;
import com.prography.game.domain.room.response.RoomResponse;
import com.prography.game.domain.user.entity.User;
import com.prography.game.domain.user.response.UserListResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static com.prography.game.domain.room.entity.RoomStatus.*;
import static com.prography.game.domain.room.entity.RoomType.DOUBLE;
import static com.prography.game.domain.room.entity.RoomType.SINGLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoomControllerTest extends ControllerTest {

    @DisplayName("방 생성 API")
    @Test
    void createRoom() throws Exception {

        // Given
        RoomRequest request = new RoomRequest();
        request.setUserId(1L);
        request.setRoomType(SINGLE);
        request.setTitle("room");

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/room")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."));
    }

    @DisplayName("방 전체 조회 API")
    @Test
    void getRoomList() throws Exception {

        // Given
        List<RoomListResponse.Room> roomList = List.of(
                new RoomListResponse.Room(1L, "room1", 4L, SINGLE, WAIT),
                new RoomListResponse.Room(2L, "room2", 5L, DOUBLE, PROGRESS),
                new RoomListResponse.Room(3L, "room3", 6L, DOUBLE, FINISH));

        given(roomService.getRoomList(any()))
                .willReturn(new RoomListResponse(3L, 1, roomList));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/room")
                        .queryParam("size", "10")
                        .queryParam("page", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result.roomList[*].id", Matchers.contains(1, 2, 3)))
                .andExpect(jsonPath("$.result.roomList[*].title", Matchers.contains("room1", "room2", "room3")))
                .andExpect(jsonPath("$.result.roomList[*].hostId", Matchers.contains(4, 5, 6)))
                .andExpect(jsonPath("$.result.roomList[*].roomType", Matchers.contains("SINGLE", "DOUBLE", "DOUBLE")))
                .andExpect(jsonPath("$.result.roomList[*].status", Matchers.contains("WAIT", "PROGRESS", "FINISH")));
    }

    @DisplayName("방 상세 조회 API")
    @Test
    void getRoom() throws Exception {

        // Given
        given(roomService.getRoom(any()))
                .willReturn(new RoomResponse(
                        1L,
                        "room",
                        2L,
                        DOUBLE,
                        WAIT,
                        LocalDateTime.of(2023, 2, 3, 11, 45, 15),
                        LocalDateTime.of(2023, 2, 3, 12, 11, 3)));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/room/{roodId}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.title").value("room"))
                .andExpect(jsonPath("$.result.hostId").value(2))
                .andExpect(jsonPath("$.result.roomType").value("DOUBLE"))
                .andExpect(jsonPath("$.result.status").value("WAIT"))
                .andExpect(jsonPath("$.result.createdAt").value("2023-02-03 11:45:15"))
                .andExpect(jsonPath("$.result.updatedAt").value("2023-02-03 12:11:03"));
    }

    @DisplayName("방 참가 API")
    @Test
    void attendRoom() throws Exception {

        // Given
        RoomAttentionRequest request = new RoomAttentionRequest();
        request.setUserId(1L);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/room/attention/{roomId}", 1)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."));
    }

    @DisplayName("방 나가기 API")
    @Test
    void leaveRoom() throws Exception {

        // Given
        RoomOutRequest request = new RoomOutRequest();
        request.setUserId(1L);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/room/out/{roomId}", 1)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."));
    }

    @DisplayName("게임시작 API")
    @Test
    void startGame() throws Exception {

        // Given
        GameStartRequest request = new GameStartRequest();
        request.setUserId(1L);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/room/start/{roomId}", 1)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."));
    }

    @DisplayName("팀 변경 API")
    @Test
    void changeTeam() throws Exception {

        // Given
        TeamChangeRequest request = new TeamChangeRequest();
        request.setUserId(1L);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/team/{roomId}", 1)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."));
    }
}
