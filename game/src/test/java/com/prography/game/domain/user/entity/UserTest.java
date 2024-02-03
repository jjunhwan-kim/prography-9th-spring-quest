package com.prography.game.domain.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.prography.game.domain.user.entity.UserStatus.*;
import static com.prography.game.global.common.response.ExceptionMessage.USER_NOT_ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @DisplayName("유저 생성시 fakerId 값이 30 이하이면 활성상태로 생성된다.")
    @Test
    void user1() {

        // Given
        User user = new User(30L, "user", "");

        // When & Then
        assertThat(user.getStatus()).isEqualTo(ACTIVE);
    }

    @DisplayName("유저 생성시 fakerId 값이 31 이상 60 이하이면 대기상태로 생성된다.")
    @Test
    void user2() {

        // Given
        User user1 = new User(31L, "user", "");
        User user2 = new User(60L, "user", "");

        // When & Then
        assertThat(user1.getStatus()).isEqualTo(WAIT);
        assertThat(user2.getStatus()).isEqualTo(WAIT);
    }

    @DisplayName("유저 생성시 fakerId 값이 61 이상이면 비활성상태로 생성된다.")
    @Test
    void user3() {

        // Given
        User user = new User(61L, "user", "");

        // When & Then
        assertThat(user.getStatus()).isEqualTo(NON_ACTIVE);
    }

    @DisplayName("Active 상태 체크시 Active 상태이면 아무일도 일어나지 않는다.")
    @Test
    void checkActiveOrElseThrow1() {

        // Given
        User user = new User(61L, "user", "");
        user.updateStatus(ACTIVE);

        // When & Then
        user.checkActiveOrElseThrow();
    }

    @DisplayName("Active 상태 체크시 Wait 상태이면 예외가 발생한다.")
    @Test
    void checkActiveOrElseThrow2() {

        // Given
        User user = new User(61L, "user", "");
        user.updateStatus(WAIT);

        // When & Then
        assertThatThrownBy(user::checkActiveOrElseThrow)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(USER_NOT_ACTIVE.getMessage());
    }

    @DisplayName("Active 상태 체크시 Non Active 상태이면 예외가 발생한다.")
    @Test
    void checkActiveOrElseThrow3() {

        // Given
        User user = new User(61L, "user", "");
        user.updateStatus(NON_ACTIVE);

        // When & Then
        assertThatThrownBy(user::checkActiveOrElseThrow)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(USER_NOT_ACTIVE.getMessage());
    }
}
