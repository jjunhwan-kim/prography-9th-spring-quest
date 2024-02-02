package com.prography.game.domain.room.entity;

import com.prography.game.domain.BaseTimeEntity;
import com.prography.game.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.prography.game.domain.room.entity.RoomStatus.*;
import static com.prography.game.global.common.response.ExceptionMessage.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Room extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User host;

    @Enumerated(STRING)
    private RoomType roomType;

    @Enumerated(STRING)
    private RoomStatus status;

    private LocalDateTime finishAt;

    private Integer redTeamCount;

    private Integer blueTeamCount;

    public Room(String title, User host, RoomType roomType) {
        this.title = title;
        this.host = host;
        this.roomType = roomType;
        this.status = WAIT;
        this.redTeamCount = 1;
        this.blueTeamCount = 0;
    }

    public void updateStatus(RoomStatus status) {
        this.status = status;
    }

    public void updateFinishAt(LocalDateTime finishAt) {
        this.finishAt = finishAt;
    }

    public void updateBlueTeamCount(Integer blueTeamCount) {
        this.blueTeamCount = blueTeamCount;
    }

    public void updateRedTeamCount(Integer redTeamCount) {
        this.redTeamCount = redTeamCount;
    }

    public void checkProgressAndUpdateIfFinish(LocalDateTime now) {
        if (PROGRESS.equals(this.status)
                && (now.isEqual(this.finishAt) || now.isAfter(this.finishAt))) {
            this.status = FINISH;
        }
    }

    public void checkWaitOrElseThrow() {
        if (!WAIT.equals(this.status)) {
            throw new IllegalStateException(ROOM_NOT_WAIT.getMessage());
        }
    }

    public void close() {
        this.redTeamCount = 0;
        this.blueTeamCount = 0;
        this.status = FINISH;
    }

    public void startGame(User user, LocalDateTime now) {

        if (!this.isHost(user)) {
            throw new IllegalStateException(USER_NOT_HOST.getMessage());
        }

        if (!this.isAllTeamFull()) {
            throw new IllegalStateException(ROOM_NOT_FULL.getMessage());
        }

        this.status = PROGRESS;
        this.finishAt = now.plusMinutes(1);
    }

    public Team attend() {
        Team team = this.getAvailableTeamOrElseThrow();
        this.increaseTeamCount(team);
        return team;
    }

    public Team getAvailableTeamOrElseThrow() {

        if (!this.isRedTeamFull()) {
            return Team.RED;
        }
        if (!this.isBlueTeamFull()) {
            return Team.BLUE;
        }

        throw new IllegalStateException(ROOM_FULL.getMessage());
    }

    public void decreaseTeamCount(Team team) {

        if (Team.RED.equals(team) && this.redTeamCount > 0) {
            this.redTeamCount--;
        } else if (Team.BLUE.equals(team) && this.blueTeamCount > 0) {
            this.blueTeamCount--;
        } else {
            throw new IllegalStateException(TEAM_CHANGE_FAIL.getMessage());
        }
    }

    public void increaseTeamCount(Team team) {

        if (Team.RED.equals(team) && !this.isRedTeamFull()) {
            this.redTeamCount++;
        } else if (Team.BLUE.equals(team) && !this.isBlueTeamFull()) {
            this.blueTeamCount++;
        } else {
            throw new IllegalStateException(TEAM_CHANGE_FAIL.getMessage());
        }
    }

    public boolean isHost(User user) {
        return this.host.getId().equals(user.getId());
    }

    public boolean isRedTeamFull() {
        if (this.redTeamCount >= this.roomType.getCount()) {
            return true;
        }
        return false;
    }

    public boolean isBlueTeamFull() {
        if (this.blueTeamCount >= this.roomType.getCount()) {
            return true;
        }
        return false;
    }

    public boolean isAllTeamFull() {
        return this.isRedTeamFull() && this.isBlueTeamFull();
    }

    public void changeTeam(UserRoom userRoom) {

        Team team = userRoom.getTeam();

        if (Team.RED.equals(team) && !isBlueTeamFull()) {
            this.decreaseTeamCount(Team.RED);
            this.increaseTeamCount(Team.BLUE);
            userRoom.updateTeam(Team.BLUE);
        } else if (Team.BLUE.equals(team) && !isRedTeamFull()) {
            this.decreaseTeamCount(Team.BLUE);
            this.increaseTeamCount(Team.RED);
            userRoom.updateTeam(Team.RED);
        } else {
            throw new IllegalStateException(TEAM_CHANGE_FAIL.getMessage());
        }
    }
}
