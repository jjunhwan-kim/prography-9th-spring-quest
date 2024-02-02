package com.prography.game.domain.user.repository;

import com.prography.game.domain.user.entity.User;
import com.prography.game.global.config.TestQuerydslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestQuerydslConfig.class)
@DataJpaTest
class UserQueryRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserQueryRepository userQueryRepository;

    @DisplayName("유저 목록을 페이징하여 가져온다.")
    @Test
    void findAll() {

        // given
        for (int i = 1; i <= 25; i++) {
            Long fakerId = (long) i;
            String name = "name " + i;
            String email = "email " + i;
            saveUser(fakerId, name, email);
        }

        // when
        Page<User> userList1 = userQueryRepository.findAll(0, 10);
        Page<User> userList2 = userQueryRepository.findAll(1, 10);
        Page<User> userList3 = userQueryRepository.findAll(2, 10);

        // then
        assertThat(userList1.getTotalElements()).isEqualTo(25);
        assertThat(userList1.getTotalPages()).isEqualTo(3);
        assertThat(userList1.getContent().size()).isEqualTo(10);

        for (int i = 0; i < 10; i++) {
            assertThat(userList1.getContent().get(i).getId()).isEqualTo(i + 1);
        }

        assertThat(userList2.getTotalElements()).isEqualTo(25);
        assertThat(userList2.getTotalPages()).isEqualTo(3);
        assertThat(userList2.getContent().size()).isEqualTo(10);

        for (int i = 0; i < 10; i++) {
            assertThat(userList2.getContent().get(i).getId()).isEqualTo(11 + i);
        }

        assertThat(userList3.getTotalElements()).isEqualTo(25);
        assertThat(userList3.getTotalPages()).isEqualTo(3);
        assertThat(userList3.getContent().size()).isEqualTo(5);

        for (int i = 0; i < 5; i++) {
            assertThat(userList3.getContent().get(i).getId()).isEqualTo(21 + i);
        }
    }

    void saveUser(Long fakerId, String name, String email) {
        userRepository.save(new User(fakerId, name, email));
    }
}
