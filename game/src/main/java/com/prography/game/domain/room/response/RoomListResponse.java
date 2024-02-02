package com.prography.game.domain.room.response;

import com.prography.game.domain.room.entity.RoomStatus;
import com.prography.game.domain.room.entity.RoomType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RoomListResponse {

    private final Long totalElements;
    private final Integer totalPages;
    private final List<Room> roomList;

    @Getter
    @RequiredArgsConstructor
    public static class Room {

        private final Long id;
        private final String title;
        private final Long hostId;
        private final RoomType roomType;
        private final RoomStatus status;

        public static Room of(com.prography.game.domain.room.entity.Room room) {
            return new Room(room.getId(),
                    room.getTitle(),
                    room.getHost().getId(),
                    room.getRoomType(),
                    room.getStatus());
        }
    }
}
