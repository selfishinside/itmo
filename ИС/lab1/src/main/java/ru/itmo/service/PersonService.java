package ru.itmo.service;

import ru.itmo.model.Person;
import java.util.List;
import java.util.Optional;

public interface PersonService {
    Optional<Person> findById(Long id);
    List<Person> findAll();
    Person save(Person person);
    Person update(Person person);
    void deleteById(Long id);
}
