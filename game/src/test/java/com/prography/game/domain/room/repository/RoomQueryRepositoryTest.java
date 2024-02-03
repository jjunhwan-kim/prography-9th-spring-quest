package com.prography.game.domain.room.repository;

import com.prography.game.domain.room.entity.Room;
import com.prography.game.global.config.TestQuerydslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import static com.prography.game.domain.room.entity.RoomType.SINGLE;
import static org.assertj.core.api.Assertions.assertThat;

@Import(TestQuerydslConfig.class)
@DataJpaTest
class RoomQueryRepositoryTest {

    @Autowired
    RoomRepository roomRepository;
    @Autowired
    RoomQueryRepository roomQueryRepository;

    @DisplayName("방 목록을 페이징하여 가져온다.")
    @Test
    void findAll() {

        // Given
        for (int i = 1; i <= 25; i++) {
            String title = "room " + i;
            saveRoom(title);
        }

        // When
        Page<Room> roomList1 = roomQueryRepository.findAll(0, 10);
        Page<Room> roomList2 = roomQueryRepository.findAll(1, 10);
        Page<Room> roomList3 = roomQueryRepository.findAll(2, 10);

        // Then
        assertThat(roomList1.getTotalElements()).isEqualTo(25);
        assertThat(roomList1.getTotalPages()).isEqualTo(3);
        assertThat(roomList1.getContent().size()).isEqualTo(10);

        for (int i = 0; i < 10; i++) {
            assertThat(roomList1.getContent().get(i).getId()).isEqualTo(i + 1);
        }

        assertThat(roomList2.getTotalElements()).isEqualTo(25);
        assertThat(roomList2.getTotalPages()).isEqualTo(3);
        assertThat(roomList2.getContent().size()).isEqualTo(10);

        for (int i = 0; i < 10; i++) {
            assertThat(roomList2.getContent().get(i).getId()).isEqualTo(11 + i);
        }

        assertThat(roomList3.getTotalElements()).isEqualTo(25);
        assertThat(roomList3.getTotalPages()).isEqualTo(3);
        assertThat(roomList3.getContent().size()).isEqualTo(5);

        for (int i = 0; i < 5; i++) {
            assertThat(roomList3.getContent().get(i).getId()).isEqualTo(21 + i);
        }
    }

    void saveRoom(String title) {
        roomRepository.save(new Room(title, null, SINGLE));
    }
}
