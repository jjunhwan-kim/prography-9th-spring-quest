package com.prography.game.domain.user.controller;

import com.prography.game.domain.ControllerTest;
import com.prography.game.domain.user.entity.UserStatus;
import com.prography.game.domain.user.response.UserListResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTest {

    @DisplayName("유저 전체 조회 API")
    @Test
    void getUserList() throws Exception {

        // given
        List<UserListResponse.User> userList = List.of(
                new UserListResponse.User(1L,
                        1L,
                        "name 1",
                        "email 1",
                        UserStatus.WAIT,
                        LocalDateTime.of(2023, 2, 2, 1, 1, 0),
                        LocalDateTime.of(2023, 2, 2, 1, 1, 0)),

                new UserListResponse.User(2L,
                        2L,
                        "name 2",
                        "email 2",
                        UserStatus.ACTIVE,
                        LocalDateTime.of(2023, 2, 2, 1, 2, 0),
                        LocalDateTime.of(2023, 2, 2, 1, 2, 0)),

                new UserListResponse.User(3L,
                        3L,
                        "name 3",
                        "email 3",
                        UserStatus.NON_ACTIVE,
                        LocalDateTime.of(2023, 2, 2, 1, 3, 0),
                        LocalDateTime.of(2023, 2, 2, 1, 3, 0)));

        given(userService.getUserList(any()))
                .willReturn(new UserListResponse(3L, 1, userList));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .queryParam("page", "0")
                        .queryParam("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result.totalElements").value(3))
                .andExpect(jsonPath("$.result.totalPages").value(1))
                .andExpect(jsonPath("$.result.userList[*].id", Matchers.contains(1, 2, 3)))
                .andExpect(jsonPath("$.result.userList[*].fakerId", Matchers.contains(1, 2, 3)))
                .andExpect(jsonPath("$.result.userList[*].name", Matchers.contains("name 1", "name 2", "name 3")))
                .andExpect(jsonPath("$.result.userList[*].email", Matchers.contains("email 1", "email 2", "email 3")))
                .andExpect(jsonPath("$.result.userList[*].status", Matchers.contains("WAIT", "ACTIVE", "NON_ACTIVE")))
                .andExpect(jsonPath("$.result.userList[*].createdAt", Matchers.contains("2023-02-02 01:01:00", "2023-02-02 01:02:00", "2023-02-02 01:03:00")))
                .andExpect(jsonPath("$.result.userList[*].updatedAt", Matchers.contains("2023-02-02 01:01:00", "2023-02-02 01:02:00", "2023-02-02 01:03:00")));
    }
}
