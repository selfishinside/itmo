package ru.itmo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class GenericDAOImpl<T, K extends Serializable> implements GenericDAO<T, K> {

    @PersistenceContext(unitName = "my-persistence-unit")
    protected EntityManager entityManager;

    /**
     * Возвращает класс сущности, с которой работает конкретный DAO.
     * Реализация предоставляется в подклассах (например, DragonDAOImpl).
     * @return класс сущности
     */
    protected abstract Class<T> getEntityClass();

    /**
     * Сохраняет новую сущность через `EntityManager.persist`.
     * @param entity сущность для сохранения
     * @return сохранённая сущность (тот же экземпляр, PK может быть заполнен)
     */
    @Override
    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    /**
     * Сохраняет сущность и выполняет `flush` — используется, когда нужно
     * принудительно синхронизировать состояние с базой данных.
     * @param entity сущность для сохранения
     * @return сохранённая сущность
     */
    @Override
    public T saveAndFlush(T entity) {
        entityManager.persist(entity);
        entityManager.flush(); // Принудительная синхронизация с БД
        return entity;
    }

    /**
     * Находит сущность по первичному ключу.
     * @param id первичный ключ
     * @return Optional с сущностью или пустой Optional, если не найдено
     */
    @Override
    public Optional<T> findById(K id) {
        return Optional.ofNullable(entityManager.find(getEntityClass(), id));
    }

    /**
     * Обновляет сущность через `EntityManager.merge`.
     * @param entity сущность с изменениями
     * @return управляемый экземпляр сущности
     */
    @Override
    public T update(T entity) {
        return entityManager.merge(entity);
    }

    /**
     * Удаляет сущность. Если сущность не находится в текущем контексте,
     * сначала выполняется `merge`, затем `remove`.
     * @param entity удаляемая сущность
     */
    @Override
    public void delete(T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    /**
     * Возвращает список всех сущностей данного типа, используя Criteria API.
     * @return список сущностей
     */
    @Override
    public List<T> findAll() {
        Class<T> entityClass = getEntityClass();
        CriteriaQuery<T> criteria = entityManager.getCriteriaBuilder().createQuery(entityClass);
        criteria.select(criteria.from(entityClass));
        return entityManager.createQuery(criteria).getResultList();
    }
}
