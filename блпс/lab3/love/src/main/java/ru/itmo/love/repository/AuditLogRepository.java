package ru.itmo.love.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.love.entity.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

 // репозиторий логов действий
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
 // ищет логи по сущности и id
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

 // ищет логи по действию и периоду
    List<AuditLog> findByActionAndTimestampBetween(String action, LocalDateTime start, LocalDateTime end);

 // ищет логи по статусу
    List<AuditLog> findByStatus(String status);
}
