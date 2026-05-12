package com.example.job_code.controller;

import com.example.job_code.model.AppUser;
import com.example.job_code.model.Notification;
import com.example.job_code.service.CurrentUserService;
import com.example.job_code.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public NotificationController(NotificationService notificationService,
                                  CurrentUserService currentUserService) {
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String viewNotifications(Model model) {
        AppUser user = currentUserService.getCurrentUser();
        List<Notification> notifications = notificationService.getNotifications(user.getId());

        model.addAttribute("notifications", notifications);
        return "notifications";
    }

    @GetMapping("/count")
    @ResponseBody
    public Map<String, Long> getUnreadCount() {
        AppUser user = currentUserService.getCurrentUser();
        long count = notificationService.getUnreadCount(user.getId());
        return Map.of("count", count);
    }

    @PostMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        AppUser user = currentUserService.getCurrentUser();
        notificationService.markAsRead(id, user.getId());
        return "redirect:/notifications";
    }

    @PostMapping("/read-all")
    public String markAllAsRead() {
        AppUser user = currentUserService.getCurrentUser();
        notificationService.markAllAsRead(user.getId());
        return "redirect:/notifications";
    }
}
