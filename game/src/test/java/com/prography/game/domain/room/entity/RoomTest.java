package com.prography.game.domain.room.entity;

import com.prography.game.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.prography.game.domain.room.entity.RoomStatus.*;
import static com.prography.game.domain.room.entity.RoomType.DOUBLE;
import static com.prography.game.domain.room.entity.RoomType.SINGLE;
import static com.prography.game.domain.room.entity.Team.BLUE;
import static com.prography.game.domain.room.entity.Team.RED;
import static com.prography.game.global.common.response.ExceptionMessage.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomTest {

    @DisplayName("Progress 상태에서 59초가 지났을 때 상태는 변하지 않는다.")
    @Test
    void checkProgressAndUpdateIfFinish1() {

        // Given
        LocalDateTime now = LocalDateTime.of(2023, 2, 3, 10, 0, 0);

        Room room = new Room("room", null, null);
        room.updateStatus(PROGRESS);
        room.updateFinishAt(now.plusMinutes(1));

        // When
        room.checkProgressAndUpdateIfFinish(now.plusMinutes(1).minusSeconds(1));

        // Then
        assertThat(room.getStatus()).isEqualTo(PROGRESS);
    }

    @DisplayName("Progress 상태에서 1분이 지났을 때 Finish 상태로 변경된다.")
    @Test
    void checkProgressAndUpdateIfFinish2() {

        // Given
        LocalDateTime now = LocalDateTime.of(2023, 2, 3, 10, 0, 0);

        Room room = new Room("room", null, null);
        room.updateStatus(PROGRESS);
        room.updateFinishAt(now.plusMinutes(1));

        // When
        room.checkProgressAndUpdateIfFinish(now.plusMinutes(1));

        // Then
        assertThat(room.getStatus()).isEqualTo(FINISH);
    }

    @DisplayName("Wait 상태 체크시 Wait 상태이면 아무일도 일어나지 않는다.")
    @Test
    void checkWaitOrElseThrow1() {

        // Given
        Room room = new Room("room", null, null);
        room.updateStatus(WAIT);

        // When & Then
        room.checkWaitOrElseThrow();
    }

    @DisplayName("Wait 상태 체크시 Progress 상태 이면 예외가 발생한다.")
    @Test
    void checkWaitOrElseThrow2() {

        // Given
        Room room = new Room("room", null, null);
        room.updateStatus(PROGRESS);

        // When & Then
        assertThatThrownBy(room::checkWaitOrElseThrow)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ROOM_NOT_WAIT.getMessage());

    }

    @DisplayName("Wait 상태 체크시 Finish 상태 이면 예외가 발생한다.")
    @Test
    void checkWaitOrElseThrow3() {

        // Given
        Room room = new Room("room", null, null);
        room.updateStatus(FINISH);

        // When & Then
        assertThatThrownBy(room::checkWaitOrElseThrow)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ROOM_NOT_WAIT.getMessage());
    }

    @DisplayName("방이 종료되면 Finish 상태로 변경 되고 각 팀원 카운트가 0으로 변경된다.")
    @Test
    void close() {

        // Given
        Room room = new Room("room", null, null);

        // When
        room.close();

        // Then
        assertThat(room.getStatus()).isEqualTo(FINISH);
        assertThat(room.getRedTeamCount()).isEqualTo(0);
        assertThat(room.getBlueTeamCount()).isEqualTo(0);
    }

    @DisplayName("게임이 정상적으로 시작되면 Progess 상태로 변경되고 종료 시간이 시작 시간 + 1분으로 업데이트 된다.")
    @Test
    void startGame1() {

        // Given
        LocalDateTime now = LocalDateTime.of(2023, 2, 3, 10, 0, 0);

        User host = new User(1L, "host", "");
        host.updateId(1L);

        Room room = new Room("room", host, SINGLE);
        room.updateBlueTeamCount(1);
        room.updateRedTeamCount(1);

        // When
        room.startGame(host, now);

        // Then
        assertThat(room.getStatus()).isEqualTo(PROGRESS);
        assertThat(room.getFinishAt()).isEqualTo(now.plusMinutes(1));
    }

    @DisplayName("호스트가 아닌 유저에 의해 게임을 시작하면 예외가 발생한다.")
    @Test
    void startGame2() {

        // Given
        LocalDateTime now = LocalDateTime.of(2023, 2, 3, 10, 0, 0);

        User host = new User(1L, "host", "");
        host.updateId(1L);

        User user = new User(1L, "host", "");
        host.updateId(2L);

        Room room = new Room("room", host, SINGLE);
        room.updateBlueTeamCount(1);
        room.updateRedTeamCount(1);

        // When & Then
        assertThatThrownBy(() -> room.startGame(user, now))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(USER_NOT_HOST.getMessage());
    }

    @DisplayName("방이 정원이 차지 않은 상태에서 게임을 시작하면 예외가 발생한다.")
    @Test
    void startGame3() {

        // Given
        LocalDateTime now = LocalDateTime.now();

        User host = new User(1L, "host", "");
        host.updateId(1L);

        Room room = new Room("room", host, SINGLE);

        // When & Then
        assertThatThrownBy(() -> room.startGame(host, now))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ROOM_NOT_FULL.getMessage());
    }

    @DisplayName("단식 게임에서 배정 가능한 팀이 있을 때 가능한 팀이 배정 된다.")
    @Test
    void getAvailableTeamOrElseThrow1() {

        // Given
        Room room = new Room("room", null, SINGLE);

        // When
        Team team = room.getAvailableTeamOrElseThrow();

        // Then
        assertThat(team).isEqualTo(BLUE);
    }

    @DisplayName("단식 게임에서 배정 가능한 팀이 없을 때 예외가 발생한다.")
    @Test
    void getAvailableTeamOrElseThrow2() {

        // Given
        Room room = new Room("room", null, SINGLE);

        // When
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(1);

        // Then
        assertThatThrownBy(room::getAvailableTeamOrElseThrow)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ROOM_FULL.getMessage());
    }

    @DisplayName("복식 게임에서 배정 가능한 팀이 있을 때 가능한 팀이 배정 된다.")
    @Test
    void getAvailableTeamOrElseThrow3() {

        // Given
        Room room = new Room("room", null, DOUBLE);

        // When
        Team team = room.getAvailableTeamOrElseThrow();

        // Then
        assertThat(team).isEqualTo(RED);
    }

    @DisplayName("복식 게임에서 배정 가능한 팀이 없을 때 예외가 발생한다.")
    @Test
    void getAvailableTeamOrElseThrow4() {

        // Given
        Room room = new Room("room", null, DOUBLE);
        room.updateRedTeamCount(2);
        room.updateBlueTeamCount(2);

        // WHen & Then
        assertThatThrownBy(room::getAvailableTeamOrElseThrow)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ROOM_FULL.getMessage());
    }

    @DisplayName("단식 게임에서 Red팀 카운트를 증가시킬 때 자리가 빈 상태이면 카운트가 증가한다.")
    @Test
    void increaseRedTeamCount1() {

        // Given
        Room room = new Room("room", null, SINGLE);
        room.updateRedTeamCount(0);

        // When
        room.increaseTeamCount(RED);

        // Then
        assertThat(room.getRedTeamCount()).isEqualTo(1);
    }

    @DisplayName("단식 게임에서 Red팀 카운트를 증가시킬 때 자리가 찬 상태이면 예외가 발생한다.")
    @Test
    void increaseRedTeamCount2() {

        // Given
        Room room = new Room("room", null, SINGLE);
        room.updateRedTeamCount(1);

        // When & Then
        assertThatThrownBy(() -> room.increaseTeamCount(RED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(TEAM_CHANGE_FAIL.getMessage());
    }

    @DisplayName("복식 게임에서 Red팀 카운트를 증가시킬 때 자리가 빈 상태이면 카운트가 증가한다.")
    @Test
    void increaseRedTeamCount3() {

        // Given
        Room room = new Room("room", null, DOUBLE);
        room.updateRedTeamCount(1);

        // When
        room.increaseTeamCount(RED);

        // Then
        assertThat(room.getRedTeamCount()).isEqualTo(2);
    }

    @DisplayName("복식 게임에서 Red팀 카운트를 증가시킬 때 자리가 찬 상태이면 예외가 발생한다.")
    @Test
    void increaseRedTeamCount4() {

        // Given
        Room room = new Room("room", null, DOUBLE);
        room.updateRedTeamCount(2);

        // When & Then
        assertThatThrownBy(() -> room.increaseTeamCount(RED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(TEAM_CHANGE_FAIL.getMessage());
    }

    @DisplayName("단식 게임에서 Blue팀 카운트를 증가시킬 때 자리가 빈 상태이면 카운트가 증가한다.")
    @Test
    void increaseBlueTeamCount1() {

        // Given
        Room room = new Room("room", null, SINGLE);
        room.updateBlueTeamCount(0);

        // When
        room.increaseTeamCount(BLUE);

        // Then
        assertThat(room.getBlueTeamCount()).isEqualTo(1);
    }

    @DisplayName("단식 게임에서 Blue팀 카운트를 증가시킬 때 자리가 찬 상태이면 예외가 발생한다.")
    @Test
    void increaseBlueTeamCount2() {

        // Given
        Room room = new Room("room", null, SINGLE);
        room.updateBlueTeamCount(1);

        // When & Then
        assertThatThrownBy(() -> room.increaseTeamCount(BLUE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(TEAM_CHANGE_FAIL.getMessage());
    }

    @DisplayName("복식 게임에서 Blue팀 카운트를 증가시킬 때 자리가 빈 상태이면 카운트가 증가한다.")
    @Test
    void increaseBlueTeamCount3() {

        // Given
        Room room = new Room("room", null, DOUBLE);
        room.updateBlueTeamCount(1);

        // When
        room.increaseTeamCount(BLUE);

        // Then
        assertThat(room.getBlueTeamCount()).isEqualTo(2);
    }

    @DisplayName("복식 게임에서 Blue팀 카운트를 증가시킬 때 자리가 찬 상태이면 예외가 발생한다.")
    @Test
    void increaseBlueTeamCount4() {

        // Given
        Room room = new Room("room", null, DOUBLE);
        room.updateBlueTeamCount(2);

        // When & Then
        assertThatThrownBy(() -> room.increaseTeamCount(BLUE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(TEAM_CHANGE_FAIL.getMessage());
    }

    @DisplayName("Red팀 일 때 팀 변경시 Blue 팀으로 변경된다.")
    @Test
    void changeTeam1() {

        // Given
        Room room = new Room("room", null, SINGLE);
        User host = new User(1L, "host", "");
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(0);
        UserRoom userRoom = new UserRoom(room, host, RED);

        // When
        room.changeTeam(userRoom);

        // Then
        assertThat(room.getRedTeamCount()).isEqualTo(0);
        assertThat(room.getBlueTeamCount()).isEqualTo(1);
        assertThat(userRoom.getTeam()).isEqualTo(BLUE);
    }

    @DisplayName("Blue팀 일 때 팀 변경시 Red 팀으로 변경된다.")
    @Test
    void changeTeam2() {

        // Given
        Room room = new Room("room", null, SINGLE);
        User host = new User(1L, "host", "");
        room.updateRedTeamCount(0);
        room.updateBlueTeamCount(1);
        UserRoom userRoom = new UserRoom(room, host, BLUE);

        // When
        room.changeTeam(userRoom);

        // Then
        assertThat(room.getRedTeamCount()).isEqualTo(1);
        assertThat(room.getBlueTeamCount()).isEqualTo(0);
        assertThat(userRoom.getTeam()).isEqualTo(RED);
    }

    @DisplayName("팀 변경시 팀이 이미 차 있을 경우 예외가 발생한다.")
    @Test
    void changeTeam3() {

        // Given
        Room room = new Room("room", null, SINGLE);
        User host = new User(1L, "host", "");
        room.updateRedTeamCount(1);
        room.updateBlueTeamCount(1);
        UserRoom userRoom = new UserRoom(room, host, RED);

        // When & Then
        assertThatThrownBy(() -> room.changeTeam(userRoom))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(TEAM_CHANGE_FAIL.getMessage());
    }
}
