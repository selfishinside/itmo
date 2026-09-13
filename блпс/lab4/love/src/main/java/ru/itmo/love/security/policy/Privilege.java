package ru.itmo.love.security.policy;

/** константы строковых идентификаторов привилегий доступа */
public final class Privilege {

    /** скрытый конструктор класс не предназначен для создания экземпляров */
    private Privilege() {
    }

    /** чтение данных об отелях */
    public static final String HOTEL_READ   = "PRIV_HOTEL_READ";
    /** создание отеля */
    public static final String HOTEL_CREATE = "PRIV_HOTEL_CREATE";
    /** чтение данных о номерах */
    public static final String ROOM_READ    = "PRIV_ROOM_READ";

    /** создание гостя */
    public static final String GUEST_CREATE = "PRIV_GUEST_CREATE";
    /** чтение данных о гостях */
    public static final String GUEST_READ   = "PRIV_GUEST_READ";

    /** создание бронирования */
    public static final String BOOKING_CREATE                = "PRIV_BOOKING_CREATE";
    /** чтение бронирований */
    public static final String BOOKING_READ                  = "PRIV_BOOKING_READ";
    /** подтверждение оплаты */
    public static final String BOOKING_CONFIRM_PAYMENT       = "PRIV_BOOKING_CONFIRM_PAYMENT";
    /** подтверждение бронирования отелем */
    public static final String BOOKING_CONFIRM_HOTEL         = "PRIV_BOOKING_CONFIRM_HOTEL";
    /** запрос отмены бронирования */
    public static final String BOOKING_REQUEST_CANCELLATION  = "PRIV_BOOKING_REQUEST_CANCELLATION";
    /** разрешение отмены бронирования */
    public static final String BOOKING_RESOLVE_CANCELLATION  = "PRIV_BOOKING_RESOLVE_CANCELLATION";
    /** обработка таймаута бронирования */
    public static final String BOOKING_HANDLE_TIMEOUT        = "PRIV_BOOKING_HANDLE_TIMEOUT";
    /** чтение квитанции о бронировании */
    public static final String BOOKING_RECEIPT_READ          = "PRIV_BOOKING_RECEIPT_READ";

    /** отправка рецензии гостем или пользователем */
    public static final String REVIEW_SUBMIT = "PRIV_REVIEW_SUBMIT";
    /** просмотр результатов асинхронной модерации рецензий */
    public static final String REVIEW_READ = "PRIV_REVIEW_READ";
    /** просмотр уведомлений и журнала интеграции лр3 */
    public static final String LAB3_MONITOR = "PRIV_LAB3_MONITOR";
    /** ручной демонстрационный запуск планировщиков */
    public static final String SCHEDULER_RUN = "PRIV_SCHEDULER_RUN";
}
