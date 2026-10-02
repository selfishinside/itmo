package ru.itmo.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.itmo.model.Dragon;

@ApplicationScoped
@Transactional
public class DragonDAOImpl extends GenericDAOImpl<Dragon, Integer> implements DragonDAO {
    @Override
    protected Class<Dragon> getEntityClass() {
        return Dragon.class;
    }
}
