package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;

public interface TodoRepositoryQuery {
    Todo findByIdWithUser(Long todoId);
}
