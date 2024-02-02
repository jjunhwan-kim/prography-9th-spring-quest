package com.prography.game.domain.room.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
    SINGLE(1),
    DOUBLE(2);

    private final Integer count;
}
