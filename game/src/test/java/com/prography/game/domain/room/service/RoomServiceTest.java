package com.prography.game.domain.room.service;

import com.prography.game.domain.room.entity.Room;
import com.prography.game.domain.room.entity.RoomStatus;
import com.prography.game.domain.room.entity.Team;
import com.prography.game.domain.room.entity.UserRoom;
import com.prography.game.domain.room.repository.RoomRepository;
import com.prography.game.domain.room.repository.UserRoomRepository;
import com.prography.game.domain.room.request.*;
import com.prography.game.domain.user.entity.User;
import com.prography.game.domain.user.entity.UserStatus;
import com.prography.game.domain.user.repository.UserRepository;
import com.prography.game.global.common.response.ExceptionMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.prography.game.domain.room.entity.RoomType.DOUBLE;
import static com.prography.game.domain.room.entity.RoomType.SINGLE;
import static com.prography.game.domain.room.entity.Team.BLUE;
import static com.prography.game.domain.room.entity.Team.RED;
import static com.prography.game.global.common.response.ExceptionMessage.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class RoomServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    UserRoomRepository userRoomRepository;
    @Autowired
    RoomService roomService;

    @DisplayName("방 생성시 방이 생성된다.")
    @Test
    void createRoom1() {

        // Given
        User user = new User(1L, "host", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        RoomRequest request = new RoomRequest();
        request.setUserId(user.getId());
        request.setRoomType(SINGLE);
        request.setTitle("room1");

        // When
        roomService.createRoom(request);

        // Then
        Optional<Room> optionalRoom = roomRepository.findByHost(user);
        assertThat(optionalRoom).isPresent();
        Room room = optionalRoom.get();
        assertThat(room.getStatus()).isEqualTo(RoomStatus.WAIT);
        assertThat(room.getRedTeamCount()).isEqualTo(1);

        Optional<UserRoom> optionalUserRoom = userRoomRepository.findByRoomAndUser(room, user);
        assertThat(optionalUserRoom).isPresent();
    }

    @DisplayName("방 생성시 유저가 활성 상태가 아니면 예외가 발생한다.")
    @Test
    void createRoom2() {

        // Given
        User user = new User(31L, "host", "");
        user.updateStatus(UserStatus.WAIT);
        userRepository.save(user);

        RoomRequest request = new RoomRequest();
        request.setUserId(user.getId());
        request.setRoomType(SINGLE);
        request.setTitle("room1");

        // When & Then
        assertThatThrownBy(() -> roomService.createRoom(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(USER_NOT_ACTIVE.getMessage());
    }

    @DisplayName("방 생성시 유저가 참여한 방이 있다면 예외가 발생한다.")
    @Test
    void createRoom3() {

        // Given
        User user = new User(1L, "host", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        Room room = new Room("room", user, SINGLE);
        roomRepository.save(room);

        UserRoom userRoom = new UserRoom(room, user);
        userRoomRepository.save(userRoom);

        RoomRequest request = new RoomRequest();
        request.setUserId(user.getId());
        request.setRoomType(SINGLE);
        request.setTitle("room1");

        // When & Then
        assertThatThrownBy(() -> roomService.createRoom(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(USER_ALREADY_IN_ROOM.getMessage());
    }

    @DisplayName("방 생성시 유저가 참여한 방이 종료된 상태이면 정상적으로 생성된다.")
    @Test
    void createRoom4() {

        // Given
        User user = new User(1L, "host", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        Room room = new Room("room", user, SINGLE);
        room.updateStatus(RoomStatus.FINISH);
        roomRepository.save(room);

        UserRoom userRoom = new UserRoom(room, user);
        userRoomRepository.save(userRoom);

        RoomRequest request = new RoomRequest();
        request.setUserId(user.getId());
        request.setRoomType(SINGLE);
        request.setTitle("room1");

        // When
        roomService.createRoom(request);

        // Then
        Optional<Room> optionalRoom = roomRepository.findByHostAndStatus(user, RoomStatus.WAIT);
        assertThat(optionalRoom).isPresent();
        room = optionalRoom.get();
        assertThat(room.getStatus()).isEqualTo(RoomStatus.WAIT);
        assertThat(room.getRedTeamCount()).isEqualTo(1);

        Optional<UserRoom> optionalUserRoom = userRoomRepository.findByRoomAndUser(room, user);
        assertThat(optionalUserRoom).isPresent();
    }

    @DisplayName("방 참가시 방에 참가된다.")
    @Test
    void attendRoom1() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.WAIT);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        Long roomId = room.getId();
        RoomAttentionRequest request = new RoomAttentionRequest();
        request.setUserId(user.getId());

        // When
        roomService.attendRoom(roomId, request);

        // Then
        room = roomRepository.findById(roomId)
                .orElseThrow();

        assertThat(room.isAllTeamFull()).isTrue();

        Optional<UserRoom> optionalUserRoom = userRoomRepository.findByRoomAndUser(room, user);
        assertThat(optionalUserRoom).isPresent();
    }

    @DisplayName("방 참가시 방이 대기 상태가 아니면 예외가 발생한다.")
    @Test
    void attendRoom2() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.PROGRESS);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // When & Then
        Long roomId = room.getId();
        RoomAttentionRequest request = new RoomAttentionRequest();
        request.setUserId(user.getId());

        assertThatThrownBy(() -> roomService.attendRoom(roomId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ROOM_NOT_WAIT.getMessage());
    }

    @DisplayName("방 참가시 유저가 활성 상태가 아니면 예외가 발생한다.")
    @Test
    void attendRoom3() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.WAIT);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.NON_ACTIVE);
        userRepository.save(user);

        // When & Then
        Long roomId = room.getId();
        RoomAttentionRequest request = new RoomAttentionRequest();
        request.setUserId(user.getId());

        assertThatThrownBy(() -> roomService.attendRoom(roomId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(USER_NOT_ACTIVE.getMessage());
    }

    @DisplayName("방 참가시 유저가 다른 방에 참여중이면 예외가 발생한다.")
    @Test
    void attendRoom4() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.WAIT);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        Room otherRoom = new Room("other room", host, SINGLE);
        otherRoom.updateStatus(RoomStatus.WAIT);
        roomRepository.save(otherRoom);

        UserRoom otherUserRoom = new UserRoom(otherRoom, user);
        userRoomRepository.save(otherUserRoom);

        // When & Then
        Long roomId = room.getId();
        RoomAttentionRequest request = new RoomAttentionRequest();
        request.setUserId(user.getId());

        assertThatThrownBy(() -> roomService.attendRoom(roomId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(USER_ALREADY_IN_ROOM.getMessage());
    }

    @DisplayName("방 참가시 유저가 참여한 다른 방이 종료된 상태이면 정상적으로 참가된다.")
    @Test
    void attendRoom5() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.WAIT);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        Room otherRoom = new Room("other room", host, SINGLE);
        otherRoom.updateStatus(RoomStatus.FINISH);
        roomRepository.save(otherRoom);

        UserRoom otherUserRoom = new UserRoom(otherRoom, user);
        userRoomRepository.save(otherUserRoom);

        Long roomId = room.getId();
        RoomAttentionRequest request = new RoomAttentionRequest();
        request.setUserId(user.getId());

        // When
        roomService.attendRoom(roomId, request);

        // Then
        room = roomRepository.findById(roomId)
                .orElseThrow();

        assertThat(room.isAllTeamFull()).isTrue();

        Optional<UserRoom> optionalUserRoom = userRoomRepository.findByRoomAndUser(room, user);
        assertThat(optionalUserRoom).isPresent();
    }

    @DisplayName("방 참가시 방의 정원이 찬 상태이면 예외가 발생한다.")
    @Test
    void attendRoom6() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.WAIT);
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(1);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);


        // When & Then
        Long roomId = room.getId();
        RoomAttentionRequest request = new RoomAttentionRequest();
        request.setUserId(user.getId());

        assertThatThrownBy(() -> roomService.attendRoom(roomId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ROOM_FULL.getMessage());
    }

    @DisplayName("유저가 방에서 나갈시 방에서 나가진다.")
    @Test
    void leaveRoom1() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.WAIT);
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(1);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        hostUserRoom.updateTeam(RED);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        UserRoom userRoom = new UserRoom(room, user);
        userRoom.updateTeam(BLUE);
        userRoomRepository.save(userRoom);

        Long roomId = room.getId();
        RoomOutRequest request = new RoomOutRequest();
        request.setUserId(user.getId());

        // When
        roomService.leaveRoom(roomId, request);

        // Then
        room = roomRepository.findById(roomId)
                .orElseThrow();

        assertThat(room.getRedTeamCount()).isEqualTo(1);
        assertThat(room.getBlueTeamCount()).isEqualTo(0);

        Optional<UserRoom> optionalUserRoom = userRoomRepository.findByRoomAndUser(room, user);
        assertThat(optionalUserRoom).isEmpty();
    }

    @DisplayName("호스트가 방에서 나갈시 방이 종료된다.")
    @Test
    void leaveRoom2() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.WAIT);
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(1);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        hostUserRoom.updateTeam(RED);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        UserRoom userRoom = new UserRoom(room, user);
        userRoom.updateTeam(BLUE);
        userRoomRepository.save(userRoom);

        Long roomId = room.getId();
        RoomOutRequest request = new RoomOutRequest();
        request.setUserId(host.getId());

        // When
        roomService.leaveRoom(roomId, request);

        // Then
        room = roomRepository.findById(roomId)
                .orElseThrow();

        assertThat(room.getStatus()).isEqualTo(RoomStatus.FINISH);
        assertThat(room.getRedTeamCount()).isEqualTo(0);
        assertThat(room.getBlueTeamCount()).isEqualTo(0);

        Optional<UserRoom> optionalHostUserRoom = userRoomRepository.findByRoomAndUser(room, host);
        assertThat(optionalHostUserRoom).isPresent();

        Optional<UserRoom> optionalUserRoom = userRoomRepository.findByRoomAndUser(room, user);
        assertThat(optionalUserRoom).isPresent();
    }

    @DisplayName("유저가 참가한 방이 아닌 방에서 나갈시 예외가 발생한다.")
    @Test
    void leaveRoom3() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.WAIT);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        hostUserRoom.updateTeam(RED);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        Long roomId = room.getId();
        RoomOutRequest request = new RoomOutRequest();
        request.setUserId(user.getId());

        // When & Then
        assertThatThrownBy(() -> roomService.leaveRoom(roomId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(USER_NOT_IN_ROOM.getMessage());
    }

    @DisplayName("시작 상태인 방에서 나갈시 예외가 발생한다.")
    @Test
    void leaveRoom4() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.PROGRESS);
        roomRepository.save(room);

        Long roomId = room.getId();
        RoomOutRequest request = new RoomOutRequest();
        request.setUserId(host.getId());

        // When & Then
        assertThatThrownBy(() -> roomService.leaveRoom(roomId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ROOM_NOT_WAIT.getMessage());
    }

    @DisplayName("종료 상태인 방에서 나갈시 예외가 발생한다.")
    @Test
    void leaveRoom5() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.FINISH);
        roomRepository.save(room);

        Long roomId = room.getId();
        RoomOutRequest request = new RoomOutRequest();
        request.setUserId(host.getId());

        // When & Then
        assertThatThrownBy(() -> roomService.leaveRoom(roomId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ROOM_NOT_WAIT.getMessage());
    }

    @DisplayName("게임 시작시 진행중 상태로 변경된다.")
    @Test
    void startGame1() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.WAIT);
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(1);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        hostUserRoom.updateTeam(RED);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        UserRoom userRoom = new UserRoom(room, user);
        userRoom.updateTeam(BLUE);
        userRoomRepository.save(userRoom);

        Long roomId = room.getId();
        GameStartRequest request = new GameStartRequest();
        request.setUserId(host.getId());

        // When
        roomService.startGame(roomId, request);

        // Then
        room = roomRepository.findById(roomId)
                .orElseThrow();

        assertThat(room.getStatus()).isEqualTo(RoomStatus.PROGRESS);
        assertThat(room.getFinishAt()).isNotNull();
    }

    @DisplayName("게임 시작시 호스트가 아니면 예외가 발생한다.")
    @Test
    void startGame2() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.WAIT);
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(1);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        hostUserRoom.updateTeam(RED);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        UserRoom userRoom = new UserRoom(room, user);
        userRoom.updateTeam(BLUE);
        userRoomRepository.save(userRoom);

        Long roomId = room.getId();
        GameStartRequest request = new GameStartRequest();
        request.setUserId(user.getId());

        // When & Then
        assertThatThrownBy(() -> roomService.startGame(roomId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(USER_NOT_HOST.getMessage());
    }

    @DisplayName("게임 시작시 정원이 맞지 않으면 예외가 발생한다.")
    @Test
    void startGame3() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.WAIT);
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(0);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        hostUserRoom.updateTeam(RED);
        userRoomRepository.save(hostUserRoom);

        Long roomId = room.getId();
        GameStartRequest request = new GameStartRequest();
        request.setUserId(host.getId());

        // When & Then
        assertThatThrownBy(() -> roomService.startGame(roomId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ROOM_NOT_FULL.getMessage());
    }

    @DisplayName("게임 시작시 방이 대기 상태가 아니면 예외가 발생한다.")
    @Test
    void startGame4() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.PROGRESS);
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(1);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        hostUserRoom.updateTeam(RED);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        UserRoom userRoom = new UserRoom(room, user);
        userRoom.updateTeam(BLUE);
        userRoomRepository.save(userRoom);

        Long roomId = room.getId();
        GameStartRequest request = new GameStartRequest();
        request.setUserId(host.getId());

        // When & Then
        assertThatThrownBy(() -> roomService.startGame(roomId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ROOM_NOT_WAIT.getMessage());
    }

    @DisplayName("팀 변경시 팀이 변경된다.")
    @Test
    void changeTeam1() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, DOUBLE);
        room.updateStatus(RoomStatus.WAIT);
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(1);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        hostUserRoom.updateTeam(RED);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        UserRoom userRoom = new UserRoom(room, user);
        userRoom.updateTeam(BLUE);
        userRoomRepository.save(userRoom);

        Long roomId = room.getId();
        TeamChangeRequest request = new TeamChangeRequest();
        request.setUserId(host.getId());

        // When
        roomService.changeTeam(roomId, request);

        // Then
        room = roomRepository.findById(roomId)
                .orElseThrow();

        assertThat(room.getRedTeamCount()).isEqualTo(0);
        assertThat(room.getBlueTeamCount()).isEqualTo(2);
        hostUserRoom = userRoomRepository.findByRoomAndUser(room, host)
                .orElseThrow();
        assertThat(hostUserRoom.getTeam()).isEqualTo(BLUE);

        // When
        request.setUserId(user.getId());
        roomService.changeTeam(roomId, request);

        room = roomRepository.findById(roomId)
                .orElseThrow();

        // Then
        assertThat(room.getRedTeamCount()).isEqualTo(1);
        assertThat(room.getBlueTeamCount()).isEqualTo(1);
        userRoom = userRoomRepository.findByRoomAndUser(room, user)
                .orElseThrow();
        assertThat(userRoom.getTeam()).isEqualTo(RED);
    }

    @DisplayName("팀 변경시 방이 대기 상태가 아니면 예외가 발생한다.")
    @Test
    void changeTeam2() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.PROGRESS);
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(1);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        hostUserRoom.updateTeam(RED);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        UserRoom userRoom = new UserRoom(room, user);
        userRoom.updateTeam(BLUE);
        userRoomRepository.save(userRoom);

        Long roomId = room.getId();
        TeamChangeRequest request = new TeamChangeRequest();
        request.setUserId(host.getId());

        // When & Then
        assertThatThrownBy(() -> roomService.changeTeam(roomId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ROOM_NOT_WAIT.getMessage());
    }

    @DisplayName("팀 변경시 유저가 방에 참가한 상태가 아니면 예외가 발생한다.")
    @Test
    void changeTeam3() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, DOUBLE);
        room.updateStatus(RoomStatus.WAIT);
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(0);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        hostUserRoom.updateTeam(RED);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        Long roomId = room.getId();
        TeamChangeRequest request = new TeamChangeRequest();
        request.setUserId(user.getId());

        // When & Then
        assertThatThrownBy(() -> roomService.changeTeam(roomId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(USER_NOT_IN_ROOM.getMessage());
    }

    @DisplayName("팀 변경시 팀 정원이 없다면 예외가 발생한다.")
    @Test
    void changeTeam4() {

        // Given
        User host = new User(1L, "host", "");
        host.updateStatus(UserStatus.ACTIVE);
        userRepository.save(host);

        Room room = new Room("room", host, SINGLE);
        room.updateStatus(RoomStatus.WAIT);
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(1);
        roomRepository.save(room);

        UserRoom hostUserRoom = new UserRoom(room, host);
        hostUserRoom.updateTeam(RED);
        userRoomRepository.save(hostUserRoom);

        User user = new User(2L, "user", "");
        user.updateStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        UserRoom userRoom = new UserRoom(room, user);
        userRoom.updateTeam(BLUE);
        userRoomRepository.save(userRoom);

        Long roomId = room.getId();
        TeamChangeRequest request = new TeamChangeRequest();
        request.setUserId(host.getId());

        // When & Then
        assertThatThrownBy(() -> roomService.changeTeam(roomId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(TEAM_CHANGE_FAIL.getMessage());
    }
}
