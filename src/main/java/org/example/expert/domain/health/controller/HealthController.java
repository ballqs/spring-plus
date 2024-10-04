package org.example.expert.domain.health.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/health")
@RestController
public class HealthController {

    @GetMapping
    public String health() {
        return "OK";
    }
}
