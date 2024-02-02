package com.prography.game.domain.user.entity;

import com.prography.game.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.prography.game.domain.user.entity.UserStatus.*;
import static com.prography.game.global.common.response.ExceptionMessage.USER_NOT_ACTIVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long fakerId;

    private String name;

    private String email;

    @Enumerated(STRING)
    private UserStatus status;

    public void updateId(Long id) {
        this.id = id;
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
    }

    public User(Long fakerId, String name, String email) {
        this.fakerId = fakerId;
        this.name = name;
        this.email = email;

        if (fakerId <= 30) {
            this.status = ACTIVE;
        } else if (fakerId <= 60) {
            this.status = WAIT;
        } else {
            this.status = NON_ACTIVE;
        }
    }

    public void checkActiveOrElseThrow() {
        if (!UserStatus.ACTIVE.equals(this.status)) {
            throw new IllegalStateException(USER_NOT_ACTIVE.getMessage());
        }
    }
}
