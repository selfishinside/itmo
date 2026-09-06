package ru.itmo.love.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.love.entity.AuditLog;
import ru.itmo.love.repository.AuditLogRepository;

import java.time.LocalDateTime;

// пишет аудит событий
@Service
@RequiredArgsConstructor
public class AuditService implements AuditServiceInt {

    private final AuditLogRepository auditLogRepository;

    @Override
    // сохраняет запись аудита
    public void logAction(String action, String entityType, Long entityId, String status, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .timestamp(LocalDateTime.now())
                .details(details)
                .status(status)
                .build();
        auditLogRepository.save(log);
    }
}
