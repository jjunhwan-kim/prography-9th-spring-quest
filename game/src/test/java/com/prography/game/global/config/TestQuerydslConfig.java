package com.prography.game.global.config;

import com.prography.game.domain.room.repository.RoomQueryRepository;
import com.prography.game.domain.user.repository.UserQueryRepository;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@TestConfiguration
public class TestQuerydslConfig {

    private final EntityManager entityManager;
    @Bean
    public JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Bean
    public UserQueryRepository userQueryRepository() {
        return new UserQueryRepository(queryFactory());
    }

    @Bean
    public RoomQueryRepository roomQueryRepository() {
        return new RoomQueryRepository(queryFactory());
    }
}
