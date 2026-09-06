package ru.itmo.love.service;

import ru.itmo.love.dto.CreateGuestRequest;
import ru.itmo.love.entity.Guest;

import java.util.List;

// контракт сервиса гостей
public interface GuestServiceInt {

    // создает гостя
    Guest createGuest(CreateGuestRequest request);

    // возвращает гостя по id
    Guest getGuestById(Long guestId);

    // возвращает гостя по email
    Guest getGuestByEmail(String email);

    // возвращает всех гостей
    List<Guest> getAllGuests();
}
