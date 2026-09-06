package ru.itmo.love.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.love.dto.CreateGuestRequest;
import ru.itmo.love.entity.Guest;
import ru.itmo.love.repository.GuestRepository;
import java.util.List;

// сервис гостей
@Service
@RequiredArgsConstructor
@Slf4j
public class GuestService implements GuestServiceInt {

    private final GuestRepository guestRepository;

    // создает гостя
    @Transactional
    @Override
    public Guest createGuest(CreateGuestRequest request) {
        log.info("Creating guest: {} {}", request.getFirstName(), request.getLastName());

        // проверяет что email свободен
        if (guestRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Guest with this email already exists");
        }

        Guest guest = Guest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .nationality(request.getNationality())
                .passportNumber(request.getPassportNumber())
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .build();

        guest = guestRepository.save(guest);
        log.info("Guest created with id {}", guest.getId());
        return guest;
    }

    // получает гостя по id
    @Transactional(readOnly = true)
    @Override
    public Guest getGuestById(Long guestId) {
        return guestRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
    }

    // получает гостя по email
    @Transactional(readOnly = true)
    @Override
    public Guest getGuestByEmail(String email) {
        return guestRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
    }

    // возвращает всех гостей
    @Transactional(readOnly = true)
    @Override
    public List<Guest> getAllGuests() {
        log.info("Getting all guests");
        return guestRepository.findAll();
    }
}
