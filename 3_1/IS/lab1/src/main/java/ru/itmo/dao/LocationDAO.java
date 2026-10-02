package ru.itmo.dao;

import jakarta.ejb.Local;
import ru.itmo.model.Location;

@Local
public interface LocationDAO extends GenericDAO<Location, Long> {
}
