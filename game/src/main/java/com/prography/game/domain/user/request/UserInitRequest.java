package com.prography.game.domain.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserInitRequest {

    @NotNull
    private Integer seed;
    @NotNull
    private Integer quantity;
}
