package ru.itmo.dao;

import jakarta.ejb.Local;
import ru.itmo.model.Person;

@Local
public interface PersonDAO extends GenericDAO<Person, Long> {
}
