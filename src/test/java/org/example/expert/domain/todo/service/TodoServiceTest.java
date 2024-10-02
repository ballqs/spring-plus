package org.example.expert.domain.todo.service;

import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TodoServiceTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testSave() {
        User user = userRepository.findById(1L).orElse(new User());

        List<Todo> todos = new ArrayList<>();

        for (int i = 0; i < 200000; i++) {
            Todo todo = new Todo("제목" , "내용" , "날씨" , user);
            todos.add(todo);
        }

        String sql = "INSERT INTO todos (title , contents , weather , user_id) VALUES(? , ? , ? , ?)";

        jdbcTemplate.batchUpdate(sql,
                todos,
                todos.size(),
                (PreparedStatement ps , Todo todo) -> {
                    ps.setString(1 , todo.getTitle());
                    ps.setString(2 , todo.getContents());
                    ps.setString(3 , todo.getWeather());
                    ps.setLong(4 , todo.getUser().getId());
                });
    }
}
