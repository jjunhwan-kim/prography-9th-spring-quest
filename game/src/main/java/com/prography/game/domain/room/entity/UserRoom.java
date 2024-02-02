package com.prography.game.domain.room.entity;

import com.prography.game.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.prography.game.domain.room.entity.Team.RED;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class UserRoom {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(STRING)
    private Team team;

    public UserRoom(Room room, User user) {
        this(room, user, RED);
    }

    public UserRoom(Room room, User user, Team team) {
        this.room = room;
        this.user = user;
        this.team = team;
    }

    public void updateTeam(Team team) {
        this.team = team;
    }
}
