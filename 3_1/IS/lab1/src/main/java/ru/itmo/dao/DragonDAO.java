package ru.itmo.dao;

import jakarta.ejb.Local;
import ru.itmo.model.Dragon;

@Local
public interface DragonDAO extends GenericDAO<Dragon, Integer> {
}
