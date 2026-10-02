package ru.itmo.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.itmo.model.Location;

@ApplicationScoped
@Transactional
public class LocationDAOImpl extends GenericDAOImpl<Location, Long> implements LocationDAO {
    @Override
    protected Class<Location> getEntityClass() {
        return Location.class;
    }
}
