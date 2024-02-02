package com.prography.game.domain.room.repository;

import com.prography.game.domain.room.entity.Room;
import com.prography.game.domain.room.entity.UserRoom;
import com.prography.game.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    List<UserRoom> findByRoom(Room room);

    Optional<UserRoom> findByRoomAndUser(Room room, User user);
}
