package org.example.expert.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoGetQueryDslResponse {
    private String title;
    private Long managerCnt;
    private Long commentCnt;
}
