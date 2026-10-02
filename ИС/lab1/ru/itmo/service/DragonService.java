package ru.itmo.service;

import ru.itmo.model.Dragon;
import ru.itmo.model.DragonType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DragonService {
    Optional<Dragon> findById(Integer id);
    List<Dragon> findAll();
    Dragon save(Dragon dragon);
    Dragon update(Dragon dragon);
    void deleteById(Integer id);
    Map<String, Long> groupByHead();
    long countByType(DragonType type);
    List<Dragon> findByNameSubstring(String substring);
    Optional<Dragon> findDeepestCaveDragon();
    String sendKillersToCave(double caveDepth);
}
