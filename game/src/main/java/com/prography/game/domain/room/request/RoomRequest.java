package com.prography.game.domain.room.request;

import com.prography.game.domain.room.entity.RoomType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequest {

    @NotNull
    private Long userId;
    @NotNull
    private RoomType roomType;
    private String title;
}
