package com.prography.game.domain.room.repository;

import com.prography.game.domain.room.entity.UserRoom;
import com.prography.game.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.prography.game.domain.room.entity.QRoom.room;
import static com.prography.game.domain.room.entity.QUserRoom.userRoom;
import static com.prography.game.domain.room.entity.RoomStatus.PROGRESS;
import static com.prography.game.domain.room.entity.RoomStatus.WAIT;

@RequiredArgsConstructor
@Repository
public class UserRoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    public UserRoom findByUser(User user) {
        return queryFactory.selectFrom(userRoom)
                .join(userRoom.room, room)
                .where(room.status.eq(WAIT).or(room.status.eq(PROGRESS)),
                        userRoom.user.id.eq(user.getId()))
                .fetchOne();
    }
}
