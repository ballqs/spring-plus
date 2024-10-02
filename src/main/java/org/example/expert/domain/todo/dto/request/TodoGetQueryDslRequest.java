package org.example.expert.domain.todo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoGetQueryDslRequest {
    private int page = 1;
    private int size = 10;
    private String title;
    private String startDt;
    private String endDt;
    private String nickName;
}
