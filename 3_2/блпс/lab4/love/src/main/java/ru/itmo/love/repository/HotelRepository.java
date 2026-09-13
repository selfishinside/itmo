package ru.itmo.love.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.love.entity.Hotel;

import java.util.List;
import java.util.Optional;

 // репозиторий отелей
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
 // ищет отель по названию
    Optional<Hotel> findByName(String name);

 // ищет отели по городу
    List<Hotel> findByCity(String city);

 // ищет отели от рейтинга
    List<Hotel> findByRatingGreaterThanEqual(Integer rating);
}
