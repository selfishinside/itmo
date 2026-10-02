package ru.itmo.service;

import ru.itmo.model.Location;
import java.util.List;
import java.util.Optional;

public interface LocationService {
    Optional<Location> findById(Long id);
    List<Location> findAll();
    Location save(Location location);
    Location update(Location location);
    void deleteById(Long id);
}
