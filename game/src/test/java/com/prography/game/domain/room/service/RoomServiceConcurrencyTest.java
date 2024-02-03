package com.prography.game.domain.room.service;

import com.prography.game.domain.room.entity.Room;
import com.prography.game.domain.room.entity.RoomType;
import com.prography.game.domain.room.entity.UserRoom;
import com.prography.game.domain.room.repository.RoomRepository;
import com.prography.game.domain.room.repository.UserRoomRepository;
import com.prography.game.domain.room.request.RoomAttentionRequest;
import com.prography.game.domain.user.entity.User;
import com.prography.game.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class RoomServiceConcurrencyTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    UserRoomRepository userRoomRepository;
    @Autowired
    RoomService roomService;

    @DisplayName("방 참가 동시성 테스트")
    @Test
    void attendRoom() throws InterruptedException {

        int userCount = 20;
        int threadCount = userCount - 1;

        List<User> userList = new ArrayList<>();
        for (int i = 1; i <= userCount; i++) {

            Long fakerId = (long) i;
            String name = "name " + i;
            String email = "email " + i;
            userList.add(new User(fakerId, name, email));
        }
        userRepository.saveAll(userList);

        User host = userList.get(0);
        Room room = new Room("room", host, RoomType.DOUBLE);
        roomRepository.save(room);
        userRoomRepository.save(new UserRoom(room, host));

        Long roomId = room.getId();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            RoomAttentionRequest request = new RoomAttentionRequest();
            User user = userList.get(1 + i);
            request.setUserId(user.getId());

            executorService.submit(() -> {
                try {
                    roomService.attendRoom(roomId, request);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        room = roomRepository.findById(room.getId()).orElseThrow();
        List<UserRoom> userRoomList = userRoomRepository.findByRoom(room);

        assertThat(room.getRedTeamCount()).isEqualTo(room.getRoomType().getCount());
        assertThat(room.getBlueTeamCount()).isEqualTo(room.getRoomType().getCount());
        assertThat(userRoomList.size()).isEqualTo(room.getRoomType().getCount() * 2);
        log.info("Red Team={}, Blue Team={}", room.getRedTeamCount(), room.getBlueTeamCount());
        log.info("userRoomList.size()={}", userRoomList.size());
    }
}
