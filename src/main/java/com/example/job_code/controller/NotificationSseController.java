package com.example.job_code.controller;

import com.example.job_code.model.AppUser;
import com.example.job_code.service.CurrentUserService;
import com.example.job_code.service.SseEmitterManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
public class NotificationSseController {

    private final SseEmitterManager sseEmitterManager;
    private final CurrentUserService currentUserService;

    public NotificationSseController(SseEmitterManager sseEmitterManager,
                                     CurrentUserService currentUserService) {
        this.sseEmitterManager = sseEmitterManager;
        this.currentUserService = currentUserService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        AppUser user = currentUserService.getCurrentUser();
        return sseEmitterManager.createEmitter(user.getId());
    }
}
