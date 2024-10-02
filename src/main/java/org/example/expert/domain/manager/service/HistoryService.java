package org.example.expert.domain.manager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.manager.entity.History;
import org.example.expert.domain.manager.repository.HistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class HistoryService {
    private final HistoryRepository historyRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(Map<String , String> data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(data);

            History history = new History(jsonStr);

            historyRepository.save(history);
        } catch (JsonProcessingException e) {
            // 예외 처리
            e.printStackTrace();
        }
    }
}
