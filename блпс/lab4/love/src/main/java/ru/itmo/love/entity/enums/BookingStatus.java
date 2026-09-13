package ru.itmo.love.entity.enums;

 // статусы жизненного цикла брони
public enum BookingStatus {
    PENDING_PAYMENT, // ожидает оплаты
    PAID, // оплачено
    CONFIRMED, // подтверждено отелем
    CANCELLATION_REQUESTED, // запрошена отмена
    PAYMENT_TIMEOUT, // истекло время оплаты
    CANCELLED, // отменено
    COMPLETED // завершено
}

