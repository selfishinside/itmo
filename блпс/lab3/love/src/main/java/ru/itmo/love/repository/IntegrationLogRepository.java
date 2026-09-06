package ru.itmo.love.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.love.entity.IntegrationLog;

import java.util.List;

@Repository
/**
 * репозиторий журнала обращений к внешней eis
 */
public interface IntegrationLogRepository extends JpaRepository<IntegrationLog, Long> {
    /** возвращает историю интеграции одной бизнес сущности */
    List<IntegrationLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    /** возвращает интеграционные вызовы с указанным результатом */
    List<IntegrationLog> findByStatus(String status);
}
