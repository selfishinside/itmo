package ru.itmo.love.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.love.dto.HotelDTO;
import ru.itmo.love.dto.HotelSearchRequest;
import ru.itmo.love.dto.RoomDTO;
import ru.itmo.love.entity.Hotel;
import ru.itmo.love.entity.Room;
import ru.itmo.love.repository.HotelRepository;
import ru.itmo.love.repository.RoomRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// сервис отелей и комнат
@Service
@RequiredArgsConstructor
@Slf4j
public class HotelService implements HotelServiceInt {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    // ищет отели по фильтрам
    @Transactional(readOnly = true)
    @Override
    public List<HotelDTO> searchHotels(HotelSearchRequest request) {
        log.info("Searching hotels with filters: city={}, minRating={}", 
                request.getCity(), request.getMinRating());

        List<Hotel> hotels;

        if (request.getCity() != null && !request.getCity().isEmpty()) {
            hotels = hotelRepository.findByCity(request.getCity());
        } else {
            hotels = hotelRepository.findAll();
        }

        if (request.getMinRating() != null) {
            hotels = hotels.stream()
                    .filter(h -> h.getRating() >= request.getMinRating())
                    .collect(Collectors.toList());
        }

        if (request.getHotelName() != null && !request.getHotelName().isEmpty()) {
            hotels = hotels.stream()
                    .filter(h -> h.getName().toLowerCase().contains(request.getHotelName().toLowerCase()))
                    .collect(Collectors.toList());
        }

        return hotels.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // получает доступные комнаты
    @Transactional(readOnly = true)
    @Override
    public List<RoomDTO> getAvailableRooms(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        log.info("Getting available rooms for hotel {} between {} and {}", hotelId, checkIn, checkOut);

        List<Room> availableRooms = roomRepository.findAvailableRooms(hotelId, checkIn, checkOut);

        return availableRooms.stream()
                .map(this::convertRoomToDTO)
                .collect(Collectors.toList());
    }

    // получает отель по id
    @Transactional(readOnly = true)
    @Override
    public HotelDTO getHotelById(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
    }

    // создает отель
    @Transactional
    @Override
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        log.info("Creating hotel: {}", hotelDTO.getName());

        Hotel hotel = Hotel.builder()
                .name(hotelDTO.getName())
                .city(hotelDTO.getCity())
                .address(hotelDTO.getAddress())
                .phone(hotelDTO.getPhone())
                .rating(hotelDTO.getRating())
                .description(hotelDTO.getDescription())
                .latitude(hotelDTO.getLatitude())
                .longitude(hotelDTO.getLongitude())
                .build();

        hotel = hotelRepository.save(hotel);
        log.info("Hotel created with id {}", hotel.getId());
        return convertToDTO(hotel);
    }

    // возвращает все отели
    @Transactional(readOnly = true)
    @Override
    public List<HotelDTO> getAllHotels() {
        log.info("Getting all hotels");
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // возвращает все комнаты
    @Transactional(readOnly = true)
    @Override
    public List<RoomDTO> getAllRooms() {
        log.info("Getting all rooms");
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                .map(this::convertRoomToDTO)
                .collect(Collectors.toList());
    }

    // получает комнату по id
    @Transactional(readOnly = true)
    @Override
    public Room getRoomEntityById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
    }

    // меняет доступность комнаты
    @Transactional
    @Override
    public void setRoomAvailability(Long roomId, boolean available) {
        Room room = getRoomEntityById(roomId);
        room.setAvailable(available);
        roomRepository.save(room);
    }

    private HotelDTO convertToDTO(Hotel hotel) {
        return HotelDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .city(hotel.getCity())
                .address(hotel.getAddress())
                .phone(hotel.getPhone())
                .rating(hotel.getRating())
                .description(hotel.getDescription())
                .latitude(hotel.getLatitude())
                .longitude(hotel.getLongitude())
                .rooms(hotel.getRooms() != null ? 
                        hotel.getRooms().stream().map(this::convertRoomToDTO).collect(Collectors.toList()) : 
                        List.of())
                .build();
    }

    private RoomDTO convertRoomToDTO(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .capacity(room.getCapacity())
                .pricePerNight(room.getPricePerNight())
                .description(room.getDescription())
                .available(room.getAvailable())
                .build();
    }
}
