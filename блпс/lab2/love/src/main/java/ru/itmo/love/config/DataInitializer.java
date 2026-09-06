package ru.itmo.love.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.itmo.love.entity.*;
import ru.itmo.love.repository.*;

import java.time.LocalDate;
import java.util.Arrays;

/** компонент инициализирующий тестовые данные при первом запуске приложения */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    /** запускает инициализацию если база данных пуста */
    @Override
    public void run(String... args) {
        if (hotelRepository.count() == 0) {
            initializeData();
        }
    }

    /** создаёт тестовые гостиницы номера и гостей */
    private void initializeData() {
        Hotel hotel1 = Hotel.builder()
                .name("Grand Hotel Moscow")
                .city("Moscow")
                .address("Red Square, 1")
                .phone("+7 (495) 123-45-67")
                .rating(5)
                .description("Luxury 5-star hotel in the heart of Moscow")
                .latitude(55.7558)
                .longitude(37.6173)
                .build();

        Hotel hotel2 = Hotel.builder()
                .name("Comfort Inn")
                .city("Saint Petersburg")
                .address("Nevsky Prospect, 100")
                .phone("+7 (812) 987-65-43")
                .rating(4)
                .description("Comfortable 4-star hotel near attractions")
                .latitude(59.9311)
                .longitude(30.3609)
                .build();

        hotel1 = hotelRepository.save(hotel1);
        hotel2 = hotelRepository.save(hotel2);

        Room room1 = Room.builder()
                .hotel(hotel1)
                .roomNumber("101")
                .roomType("Single")
                .capacity(1)
                .pricePerNight(150.0)
                .description("Cozy single room with city view")
                .available(true)
                .build();

        Room room2 = Room.builder()
                .hotel(hotel1)
                .roomNumber("102")
                .roomType("Double")
                .capacity(2)
                .pricePerNight(250.0)
                .description("Spacious double room with king bed")
                .available(true)
                .build();

        Room room3 = Room.builder()
                .hotel(hotel1)
                .roomNumber("103")
                .roomType("Suite")
                .capacity(4)
                .pricePerNight(450.0)
                .description("Luxury suite with living area")
                .available(true)
                .build();

        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);

        Room room4 = Room.builder()
                .hotel(hotel2)
                .roomNumber("201")
                .roomType("Double")
                .capacity(2)
                .pricePerNight(120.0)
                .description("Standard double room")
                .available(true)
                .build();

        Room room5 = Room.builder()
                .hotel(hotel2)
                .roomNumber("202")
                .roomType("Twin")
                .capacity(2)
                .pricePerNight(110.0)
                .description("Twin beds room")
                .available(true)
                .build();

        roomRepository.save(room4);
        roomRepository.save(room5);

        Guest guest1 = Guest.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .email("ivan.petrov@example.com")
                .phone("+7 (999) 123-45-67")
                .nationality("Russian")
                .passportNumber("1234567890")
                .birthDate(LocalDate.of(1990, 5, 15))
                .address("Pushkin St, 10")
                .city("Moscow")
                .postalCode("101000")
                .country("Russia")
                .build();

        Guest guest2 = Guest.builder()
                .firstName("Maria")
                .lastName("Smirnova")
                .email("maria.smirnova@example.com")
                .phone("+7 (999) 987-65-43")
                .nationality("Russian")
                .passportNumber("0987654321")
                .birthDate(LocalDate.of(1992, 8, 22))
                .address("Nevsky Prospect, 50")
                .city("Saint Petersburg")
                .postalCode("190000")
                .country("Russia")
                .build();

        guestRepository.saveAll(Arrays.asList(guest1, guest2));

        System.out.println("✓ Test data initialized successfully!");
    }
}
