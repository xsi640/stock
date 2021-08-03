package com.github.xsi640

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager

@Configuration
class RepositoryConfiguration {
    @Bean
    fun jpaQueryFactory(entityManager: EntityManager): JPAQueryFactory {
        return JPAQueryFactory(entityManager)
    }
}