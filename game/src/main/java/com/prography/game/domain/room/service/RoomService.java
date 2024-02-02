package com.prography.game.domain.room.service;

import com.prography.game.domain.room.entity.Room;
import com.prography.game.domain.room.entity.RoomType;
import com.prography.game.domain.room.entity.Team;
import com.prography.game.domain.room.entity.UserRoom;
import com.prography.game.domain.room.repository.RoomQueryRepository;
import com.prography.game.domain.room.repository.RoomRepository;
import com.prography.game.domain.room.repository.UserRoomQueryRepository;
import com.prography.game.domain.room.repository.UserRoomRepository;
import com.prography.game.domain.room.request.*;
import com.prography.game.domain.room.response.RoomListResponse;
import com.prography.game.domain.room.response.RoomResponse;
import com.prography.game.domain.user.entity.User;
import com.prography.game.domain.user.repository.UserRepository;
import com.prography.game.global.common.response.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.prography.game.global.common.response.ExceptionMessage.USER_ALREADY_IN_ROOM;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomQueryRepository roomQueryRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final UserRoomQueryRepository userRoomQueryRepository;

    @Transactional
    public void createRoom(RoomRequest request) {

        Long userId = request.getUserId();
        RoomType roomType = request.getRoomType();
        String title = request.getTitle();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.ID_NOT_EXIST.getMessage()));

        user.checkActiveOrElseThrow();

        if (userRoomQueryRepository.findByUser(user) != null) {
            throw new IllegalStateException(USER_ALREADY_IN_ROOM.getMessage());
        }

        Room room = roomRepository.save(new Room(title, user, roomType));
        userRoomRepository.save(new UserRoom(room, user));
    }

    @Transactional
    public RoomListResponse getRoomList(RoomListRequest request) {

        Integer page = request.getPage();
        Integer size = request.getSize();

        Page<Room> rooms = roomQueryRepository.findAll(page, size);

        LocalDateTime now = LocalDateTime.now();
        rooms.getContent().forEach(room -> room.checkProgressAndUpdateIfFinish(now));

        long totalElements = rooms.getTotalElements();
        int totalPages = rooms.getTotalPages();
        List<RoomListResponse.Room> roomList = rooms.getContent().stream()
                .map(RoomListResponse.Room::of)
                .collect(Collectors.toList());

        return new RoomListResponse(totalElements, totalPages, roomList);
    }

    @Transactional
    public RoomResponse getRoom(Long roomId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.ID_NOT_EXIST.getMessage()));

        LocalDateTime now = LocalDateTime.now();
        room.checkProgressAndUpdateIfFinish(now);

        return RoomResponse.of(room);
    }

    @Transactional
    public void attendRoom(Long roomId, RoomAttentionRequest request) {

        Long userId = request.getUserId();

        Room room = roomRepository.findByIdWithPessimisticLock(roomId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.ID_NOT_EXIST.getMessage()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.ID_NOT_EXIST.getMessage()));

        room.checkWaitOrElseThrow();
        user.checkActiveOrElseThrow();

        if (userRoomQueryRepository.findByUser(user) != null) {
            throw new IllegalStateException(USER_ALREADY_IN_ROOM.getMessage());
        }

        Team team = room.attend();

        UserRoom userRoom = new UserRoom(room, user, team);
        userRoomRepository.save(userRoom);
    }

    @Transactional
    public void leaveRoom(Long roomId, RoomOutRequest request) {

        Long userId = request.getUserId();

        Room room = roomRepository.findByIdWithPessimisticLock(roomId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.ID_NOT_EXIST.getMessage()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.ID_NOT_EXIST.getMessage()));

        room.checkWaitOrElseThrow();
        user.checkActiveOrElseThrow();

        if (room.isHost(user)) {
            room.close();
        } else {
            UserRoom userRoom = userRoomRepository.findByRoomAndUser(room, user)
                    .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.USER_NOT_IN_ROOM.getMessage()));

            room.decreaseTeamCount(userRoom.getTeam());
            userRoomRepository.delete(userRoom);
        }
    }

    @Transactional
    public void startGame(Long roomId, GameStartRequest request) {

        Long userId = request.getUserId();

        Room room = roomRepository.findByIdWithPessimisticLock(roomId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.ID_NOT_EXIST.getMessage()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.ID_NOT_EXIST.getMessage()));

        room.checkWaitOrElseThrow();
        user.checkActiveOrElseThrow();

        LocalDateTime now = LocalDateTime.now();
        room.startGame(user, now);
    }

    @Transactional
    public void changeTeam(Long roomId, TeamChangeRequest request) {

        Long userId = request.getUserId();

        Room room = roomRepository.findByIdWithPessimisticLock(roomId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.ID_NOT_EXIST.getMessage()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.ID_NOT_EXIST.getMessage()));

        room.checkWaitOrElseThrow();
        user.checkActiveOrElseThrow();

        UserRoom userRoom = userRoomRepository.findByRoomAndUser(room, user)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.USER_NOT_IN_ROOM.getMessage()));

        room.changeTeam(userRoom);
    }
}
