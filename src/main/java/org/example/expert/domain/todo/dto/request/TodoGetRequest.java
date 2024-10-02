package org.example.expert.domain.todo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoGetRequest {
    private int page = 1;
    private int size = 10;
    private String weather;
    private String startDt;
    private String endDt;
}
