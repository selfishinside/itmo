package ru.itmo.love.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.love.entity.Guest;

import java.util.Optional;

 // репозиторий гостей
@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
 // ищет гостя по email
    Optional<Guest> findByEmail(String email);

 // ищет гостя по паспорту
    Optional<Guest> findByPassportNumber(String passportNumber);
}
