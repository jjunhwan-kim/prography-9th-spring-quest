package com.prography.game.domain.room.repository;

import com.prography.game.domain.room.entity.Room;
import com.prography.game.domain.room.entity.RoomStatus;
import com.prography.game.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Lock(PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    Optional<Room> findByIdWithPessimisticLock(Long id);

    Optional<Room> findByHost(User user);

    Optional<Room> findByHostAndStatus(User user, RoomStatus status);
}
