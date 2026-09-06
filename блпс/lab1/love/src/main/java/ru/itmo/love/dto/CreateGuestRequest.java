package ru.itmo.love.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// запрос на создание гостя
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGuestRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String nationality;
    private String passportNumber;
    private LocalDate birthDate;
    private String address;
    private String city;
    private String postalCode;
    private String country;
}
