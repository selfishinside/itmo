package ru.itmo.love.entity.enums;

/**
 * статусы обработки и публикации рецензии
 */
public enum ReviewStatus {
    PENDING_MODERATION, // ожидает модерирования
    APPROVED, // одобрено
    REJECTED, // отклонено
    PUBLISHED // опубликовано
}
