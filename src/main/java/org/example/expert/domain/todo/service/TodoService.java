package org.example.expert.domain.todo.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoGetRequest;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;
    private final EntityManager entityManager;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    public Page<TodoResponse> getTodos(TodoGetRequest todoGetRequest) {
        StringBuilder queryBuilder = new StringBuilder("SELECT t FROM Todo t where 1=1");

        if (Objects.nonNull(todoGetRequest.getWeather())) {
            queryBuilder.append(" AND t.weather = :weather");
        }

        if (Objects.nonNull(todoGetRequest.getStartDt())) {
            queryBuilder.append(" AND DATE_FORMAT(t.createdAt , '%Y-%m-%d') >= :startDt");
        }

        if (Objects.nonNull(todoGetRequest.getEndDt())) {
            queryBuilder.append(" AND DATE_FORMAT(t.createdAt , '%Y-%m-%d') <= :endDt");
        }

        // 쿼리
        TypedQuery<Todo> query = entityManager.createQuery(queryBuilder.toString(), Todo.class);

        if (Objects.nonNull(todoGetRequest.getWeather())) {
            query.setParameter("weather", todoGetRequest.getWeather());
        }

        if (Objects.nonNull(todoGetRequest.getStartDt())) {
            query.setParameter("startDt", todoGetRequest.getStartDt());
        }

        if (Objects.nonNull(todoGetRequest.getEndDt())) {
            query.setParameter("endDt", todoGetRequest.getEndDt());
        }

        // 페이징 처리
        Pageable pageable = PageRequest.of(todoGetRequest.getPage() - 1, todoGetRequest.getSize());
        query.setFirstResult((int) pageable.getOffset()); // 시작 위치
        query.setMaxResults(pageable.getPageSize()); // 페이지당 결과 개수

        List<Todo> todos = query.getResultList();

        // 총 개수 계산
        TypedQuery<Long> countQuery = entityManager.createQuery(
                queryBuilder.toString().replace("SELECT t", "SELECT COUNT(t)"), Long.class);


        if (Objects.nonNull(todoGetRequest.getWeather())) {
            countQuery.setParameter("weather", todoGetRequest.getWeather());
        }

        if (Objects.nonNull(todoGetRequest.getStartDt())) {
            countQuery.setParameter("startDt", todoGetRequest.getStartDt());
        }

        if (Objects.nonNull(todoGetRequest.getEndDt())) {
            countQuery.setParameter("endDt", todoGetRequest.getEndDt());
        }

        Long cnt = countQuery.getSingleResult();

        List<TodoResponse> todoResponse = todos
                .stream()
                .map(todo -> new TodoResponse(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getContents(),
                        todo.getWeather(),
                        new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                        todo.getCreatedAt(),
                        todo.getModifiedAt()
                ))
                .toList();

        return new PageImpl<>(todoResponse, pageable, cnt);
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId);

        if (Objects.isNull(todo)) {
            throw new InvalidRequestException("Todo not found");
        }

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }
}
