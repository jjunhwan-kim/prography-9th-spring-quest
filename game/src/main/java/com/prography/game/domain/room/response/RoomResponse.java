package com.prography.game.domain.room.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prography.game.domain.room.entity.Room;
import com.prography.game.domain.room.entity.RoomStatus;
import com.prography.game.domain.room.entity.RoomType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@RequiredArgsConstructor
public class RoomResponse {

    private final Long id;
    private final String title;
    private final Long hostId;
    private final RoomType roomType;
    private final RoomStatus status;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime updatedAt;

    public static RoomResponse of(Room room) {
        return new RoomResponse(room.getId(),
                room.getTitle(),
                room.getHost().getId(),
                room.getRoomType(),
                room.getStatus(),
                room.getCreatedAt(),
                room.getUpdatedAt());
    }
}
