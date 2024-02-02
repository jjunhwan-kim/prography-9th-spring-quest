package com.prography.game.domain.room.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomOutRequest {

    @NotNull
    private Long userId;
}
