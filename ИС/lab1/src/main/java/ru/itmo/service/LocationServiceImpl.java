package ru.itmo.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.itmo.dao.DragonDAO;
import ru.itmo.dao.LocationDAO;
import ru.itmo.dao.PersonDAO;
import ru.itmo.model.Dragon;
import ru.itmo.model.Location;
import ru.itmo.model.Person;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class LocationServiceImpl implements LocationService {
    @Inject
    private LocationDAO locationDAO;

    @Inject
    private PersonDAO personDAO;

    @Inject
    private DragonDAO dragonDAO;

    @Override
    public Optional<Location> findById(Long id) {
        return locationDAO.findById(id);
    }

    @Override
    public List<Location> findAll() {
        return locationDAO.findAll();
    }

    @Override
    public Location save(Location location) {
        return locationDAO.save(location);
    }

    @Override
    public Location update(Location location) {
        return locationDAO.update(location);
    }

    @Override
    public void deleteById(Long id) {
        Location location = locationDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Локация не найдена"));
        for (Person person : personDAO.findAll()) {
            if (person.getLocation() != null && id.equals(person.getLocation().getId())) {
                for (Dragon dragon : dragonDAO.findAll()) {
                    if (dragon.getKiller() != null && person.getId().equals(dragon.getKiller().getId())) {
                        dragonDAO.delete(dragon);
                    }
                }
                personDAO.delete(person);
            }
        }
        locationDAO.delete(location);
    }
}
