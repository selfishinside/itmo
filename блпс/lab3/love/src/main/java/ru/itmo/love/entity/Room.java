package ru.itmo.love.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.love.entity.Booking;

import java.util.List;

 // комната в базе
@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private String roomType; // тип комнаты

    @Column(nullable = false)
    private Integer capacity; // вместимость

    @Column(nullable = false)
    private Double pricePerNight;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Boolean available;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
}

