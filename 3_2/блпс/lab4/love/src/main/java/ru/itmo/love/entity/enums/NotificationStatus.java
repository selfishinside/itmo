package ru.itmo.love.entity.enums;

/**
 * статусы жизненного цикла уведомления включая неуспешную и повторную отправку
 */
public enum NotificationStatus {
    PENDING, // ожидает отправки
    SENT, // отправлено
    FAILED, // ошибка при отправке
    RETRY, // в очереди на повторную отправку
    DELIVERED // доставлено
}
