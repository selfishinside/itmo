package ru.itmo.love.security.policy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** сопоставление ролей с наборами привилегий доступа */
public final class RolePrivileges {

    /** скрытый конструктор — класс не предназначен для создания экземпляров */
    private RolePrivileges() {
    }

    /** роль гостя */
    public static final String ROLE_GUEST = "ROLE_GUEST";
    /** роль обычного пользователя */
    public static final String ROLE_USER = "ROLE_USER";
    /** роль администратора */
    public static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    /** роль менеджера отеля */
    public static final String ROLE_HOTEL_MANAGER = "ROLE_HOTEL_MANAGER";

    /** неизменяемая карта роль → набор привилегий */
    public static final Map<String, Set<String>> ROLE_TO_PRIVILEGES = rolePrivileges();

    /** формирует неизменяемую карту сопоставления ролей с привилегиями */
    private static Map<String, Set<String>> rolePrivileges() {
        Map<String, Set<String>> result = new LinkedHashMap<>();

        result.put(ROLE_ADMINISTRATOR, Set.of(
                Privilege.HOTEL_READ,
                Privilege.HOTEL_CREATE,
                Privilege.ROOM_READ,
                Privilege.GUEST_CREATE,
                Privilege.GUEST_READ,
                Privilege.BOOKING_CREATE,
                Privilege.BOOKING_READ,
                Privilege.BOOKING_CONFIRM_PAYMENT,
                Privilege.BOOKING_CONFIRM_HOTEL,
                Privilege.BOOKING_REQUEST_CANCELLATION,
                Privilege.BOOKING_RESOLVE_CANCELLATION,
                Privilege.BOOKING_HANDLE_TIMEOUT,
                Privilege.BOOKING_RECEIPT_READ
        ));

        result.put(ROLE_HOTEL_MANAGER, Set.of(
                Privilege.HOTEL_READ,
                Privilege.HOTEL_CREATE,
                Privilege.ROOM_READ,
                Privilege.BOOKING_READ,
                Privilege.BOOKING_CONFIRM_HOTEL,
                Privilege.BOOKING_RESOLVE_CANCELLATION
        ));

        result.put(ROLE_USER, Set.of(
                Privilege.BOOKING_CREATE,
                Privilege.BOOKING_CONFIRM_PAYMENT,
                Privilege.BOOKING_REQUEST_CANCELLATION,
                Privilege.BOOKING_READ,
                Privilege.BOOKING_RECEIPT_READ,
                Privilege.HOTEL_READ,
                Privilege.ROOM_READ,
                Privilege.GUEST_READ
        ));

        result.put(ROLE_GUEST, Set.of(
                Privilege.HOTEL_READ,
                Privilege.ROOM_READ,
                Privilege.BOOKING_CREATE,
                Privilege.BOOKING_READ,
                Privilege.BOOKING_REQUEST_CANCELLATION,
                Privilege.BOOKING_RECEIPT_READ
        ));

        return Map.copyOf(result);
    }
}
