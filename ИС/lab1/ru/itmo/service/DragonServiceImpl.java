package ru.itmo.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.itmo.dao.DragonDAO;
import ru.itmo.model.Dragon;
import ru.itmo.model.DragonType;
import ru.itmo.websocket.DragonUpdateEndpoint;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class DragonServiceImpl implements DragonService {
    @Inject
    private DragonDAO dragonDAO;

    @Inject
    private DragonUpdateEndpoint updateEndpoint;

    @Override
    public Optional<Dragon> findById(Integer id) {
        return dragonDAO.findById(id);
    }

    @Override
    public List<Dragon> findAll() {
        return dragonDAO.findAll();
    }

    @Override
    public Dragon save(Dragon dragon) {
        Dragon saved = dragonDAO.save(dragon);
        updateEndpoint.broadcastUpdate();
        return saved;
    }

    @Override
    public Dragon update(Dragon dragon) {
        Dragon updated = dragonDAO.update(dragon);
        updateEndpoint.broadcastUpdate();
        return updated;
    }

    @Override
    public void deleteById(Integer id) {
        dragonDAO.findById(id).ifPresent(dragon -> {
            dragonDAO.delete(dragon);
            updateEndpoint.broadcastUpdate();
        });
    }

    @Override
    public Map<String, Long> groupByHead() {
        return findAll().stream()
                .collect(Collectors.groupingBy(Dragon::getHeadKey, Collectors.counting()));
    }

    @Override
    public long countByType(DragonType type) {
        if (type == null) {
            return 0;
        }
        return findAll().stream().filter(dragon -> type.equals(dragon.getType())).count();
    }

    @Override
    public List<Dragon> findByNameSubstring(String substring) {
        if (substring == null || substring.isBlank()) {
            return List.of();
        }
        return findAll().stream()
                .filter(dragon -> dragon.getName() != null && dragon.getName().contains(substring))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Dragon> findDeepestCaveDragon() {
        return findAll().stream()
                .filter(dragon -> dragon.getCave() != null)
                .max(Comparator.comparingDouble(dragon -> dragon.getCave().getDepth()));
    }

    @Override
    public String sendKillersToCave(double caveDepth) {
        List<Dragon> targets = findAll().stream()
                .filter(dragon -> dragon.getCave() != null)
                .filter(dragon -> dragon.getKiller() == null)
                .filter(dragon -> Double.compare(dragon.getCave().getDepth(), caveDepth) == 0)
                .collect(Collectors.toList());
        return "Команда отправлена в пещеру depth=" + caveDepth + ". Живых целей найдено: " + targets.size();
    }
}
