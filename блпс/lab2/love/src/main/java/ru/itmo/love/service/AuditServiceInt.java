package ru.itmo.love.service;

// контракт аудита действий
public interface AuditServiceInt {

    // пишет запись аудита
    void logAction(String action, String entityType, Long entityId, String status, String details);
}
