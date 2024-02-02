package com.prography.game.domain.room.repository;

import com.prography.game.domain.room.entity.Room;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.prography.game.domain.room.entity.QRoom.room;


@RequiredArgsConstructor
@Repository
public class RoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Room> findAll(Integer page, Integer size) {

        long offset = (long) page * size;
        long limit = size;

        List<Room> content = queryFactory.selectFrom(room)
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(room.count())
                .from(room)
                .fetchOne();

        if (totalCount == null) {
            totalCount = 0L;
        }

        return new PageImpl<>(content, PageRequest.of(page, size), totalCount);
    }
}
