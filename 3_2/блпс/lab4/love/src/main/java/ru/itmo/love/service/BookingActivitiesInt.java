package ru.itmo.love.service;

/** шаги бронирования которые вызывает движок */
public interface BookingActivitiesInt extends BookingServiceInt {
    /** отменяет бронь после таймера оплаты */
    void expirePayment(Long id);
    /** завершает проживание */
    void completeStay(Long id);
}
