package ru.itmo.love.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * журнал попыток интеграции с внешней корпоративной системой bitrix
 */
@Entity
@Table(name = "integration_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String entityType; // notification review

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false, length = 100)
    private String operation; // send_to_bitrix sync_rating

    @Column(nullable = false, length = 50)
    private String status; // success failed pending

    /** идентификатор созданной сущности во внешней системе bitrix24 */
    private String externalId;

    @Column(columnDefinition = "TEXT")
    private String bitrixResponse;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
