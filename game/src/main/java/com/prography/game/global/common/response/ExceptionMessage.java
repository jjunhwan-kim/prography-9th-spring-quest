package com.prography.game.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    ID_NOT_EXIST("ID does not exist."),
    USER_NOT_ACTIVE("User status is not active."),
    USER_NOT_HOST("User is not a host."),
    ROOM_NOT_FULL("Room is not full."),
    ROOM_NOT_WAIT("Room status is not wait."),
    ROOM_FULL("Room is full."),
    USER_ALREADY_IN_ROOM("User is already in another room."),
    USER_NOT_IN_ROOM("User is not in the room."),
    TEAM_CHANGE_FAIL("Team change is not possible.");

    private final String message;
}
