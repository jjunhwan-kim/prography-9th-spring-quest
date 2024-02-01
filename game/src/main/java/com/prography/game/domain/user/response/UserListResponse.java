package com.prography.game.domain.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prography.game.domain.user.entity.UserStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@RequiredArgsConstructor
public class UserListResponse {

    private final Long totalElements;
    private final Integer totalPages;
    private final List<User> userList;

    @Getter
    @RequiredArgsConstructor
    public static class User {

        private final Long id;
        private final Long fakerId;
        private final String name;
        private final String email;
        private final UserStatus status;
        @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createdAt;
        @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime updatedAt;

        public static User of(com.prography.game.domain.user.entity.User user) {
            return new User(user.getId(),
                    user.getFakerId(),
                    user.getName(),
                    user.getEmail(),
                    user.getStatus(),
                    user.getCreatedAt(),
                    user.getUpdatedAt());
        }
    }
}
