package com.prography.game.domain.user.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserListRequest {

    @NotNull
    @Min(0)
    private Integer page;
    @NotNull
    @Min(1)
    private Integer size;
}
