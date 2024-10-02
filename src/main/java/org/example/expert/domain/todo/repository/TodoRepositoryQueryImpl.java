package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoGetQueryDslResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

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

    @Override
    public List<TodoGetQueryDslResponse> getTodoDetail(String title, String nickName, String startDt, String endDt, Pageable pageable) {
        QTodo t = QTodo.todo;
        QUser u = QUser.user;
        QComment c = QComment.comment;
        QManager m = QManager.manager;

        BooleanBuilder builder = new BooleanBuilder();
        if (title != null) {
            builder.and(t.title.contains(title));
        }

        if (nickName != null) {
            JPQLQuery<Long> subquery = JPAExpressions
                    .select(m.count())
                    .from(m)
                    .join(u).on(m.user.id.eq(u.id))
                    .where(m.todo.id.eq(t.id).and(u.nickname.contains(nickName)));
            builder.and(subquery.gt(0L));
        }

        if (startDt != null) {
            builder.and(Expressions.dateTemplate(String.class, "DATE_FORMAT({0} , '%Y-%m-%d')" , t.createdAt).goe(startDt));
        }

        if (endDt != null) {
            builder.and(Expressions.dateTemplate(String.class, "DATE_FORMAT({0} , '%Y-%m-%d')" , t.createdAt).loe(endDt));
        }

        return jpaQueryFactory
                .select(Projections.constructor(TodoGetQueryDslResponse.class ,
                        t.title,
                        m.user.id.count().as("manager_cnt"),
                        c.id.count().as("comment_cnt")))
                .from(t)
                .leftJoin(m).on(m.todo.id.eq(t.id))
                .leftJoin(c).on(c.todo.id.eq(t.id))
                .where(builder)
                .groupBy(t.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
