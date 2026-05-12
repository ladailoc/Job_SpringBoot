package com.example.job_code.service;

import com.example.job_code.model.AppUser;
import com.example.job_code.model.Notification;
import com.example.job_code.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterManager sseEmitterManager;

    public NotificationService(NotificationRepository notificationRepository,
                               SseEmitterManager sseEmitterManager) {
        this.notificationRepository = notificationRepository;
        this.sseEmitterManager = sseEmitterManager;
    }

    @Transactional
    public void createNotification(AppUser user, String message, String link) {
        Notification notification = new Notification(user, message, link);
        notificationRepository.save(notification);

        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(user.getId());
        sseEmitterManager.send(user.getId(), new NotificationEvent(message, link, unreadCount));
    }

    public record NotificationEvent(String message, String link, long unreadCount) {}


    @Transactional(readOnly = true)
    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền đánh dấu thông báo này");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
            }
        }
        notificationRepository.saveAll(notifications);
    }
}
