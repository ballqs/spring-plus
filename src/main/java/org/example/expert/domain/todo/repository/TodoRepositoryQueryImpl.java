package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;

@RequiredArgsConstructor
public class TodoRepositoryQueryImpl implements TodoRepositoryQuery{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Todo findByIdWithUser(Long todoId) {
        QTodo t = QTodo.todo;
        QUser u = QUser.user;

        return jpaQueryFactory
                .select(t)
                .from(t)
                .join(t.user , u).fetchJoin()
                .where(t.id.eq(todoId))
                .fetchFirst();
    }
}
