package ru.itmo.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.itmo.dao.DragonDAO;
import ru.itmo.dao.PersonDAO;
import ru.itmo.model.Dragon;
import ru.itmo.model.Person;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class PersonServiceImpl implements PersonService {
    @Inject
    private PersonDAO personDAO;

    @Inject
    private DragonDAO dragonDAO;

    @Override
    public Optional<Person> findById(Long id) {
        return personDAO.findById(id);
    }

    @Override
    public List<Person> findAll() {
        return personDAO.findAll();
    }

    @Override
    public Person save(Person person) {
        return personDAO.save(person);
    }

    @Override
    public Person update(Person person) {
        return personDAO.update(person);
    }

    @Override
    public void deleteById(Long id) {
        personDAO.findById(id).ifPresent(person -> {
            for (Dragon dragon : dragonDAO.findAll()) {
                if (dragon.getKiller() != null && id.equals(dragon.getKiller().getId())) {
                    dragonDAO.delete(dragon);
                }
            }
            personDAO.delete(person);
        });
    }
}
