package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoGetQueryDslResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TodoRepositoryQuery {
    Todo findByIdWithUser(Long todoId);
    List<TodoGetQueryDslResponse> getTodoDetail(String title , String nickName , String startDt , String endDt , Pageable pageable);
}
