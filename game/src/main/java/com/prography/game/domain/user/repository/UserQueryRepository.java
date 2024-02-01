package com.prography.game.domain.user.repository;

import com.prography.game.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.prography.game.domain.user.entity.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<User> findAll(Integer page, Integer size) {

        long offset = (long) page * size;
        long limit = size;

        List<User> content = queryFactory.selectFrom(user)
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(user.count())
                .from(user)
                .fetchOne();

        if (totalCount == null) {
            totalCount = 0L;
        }

        return new PageImpl<>(content, PageRequest.of(page, size), totalCount);
    }
}
