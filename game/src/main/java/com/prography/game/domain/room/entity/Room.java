package com.prography.game.domain.room.entity;

import com.prography.game.domain.BaseTimeEntity;
import com.prography.game.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User host;

    @Enumerated(STRING)
    private RoomType roomType;

    @Enumerated(STRING)
    private RoomStatus status;

    private LocalDateTime endAt;
}
