package ru.itmo.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.itmo.model.Person;

@ApplicationScoped
@Transactional
public class PersonDAOImpl extends GenericDAOImpl<Person, Long> implements PersonDAO {
    @Override
    protected Class<Person> getEntityClass() {
        return Person.class;
    }
}
