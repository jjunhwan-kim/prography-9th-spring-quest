package com.prography.game.global.config;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {

    @Bean
    public JPAQueryFactory queryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }
}
