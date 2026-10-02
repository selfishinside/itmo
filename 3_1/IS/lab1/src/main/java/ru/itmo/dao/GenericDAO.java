package ru.itmo.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Универсальный DAO-интерфейс для CRUD-операций над сущностями.
 * @param <T> тип сущности
 * @param <K> тип первичного ключа (должен быть Serializable)
 */
public interface GenericDAO<T, K extends Serializable> {

    /**
     * Сохраняет новую сущность в базе данных.
     * @param entity объект для сохранения
     * @return сохранённая сущность (возможно с заполненным PK)
     */
    T save(T entity);

    /**
     * Находит сущность по её первичному ключу.
     * @param id первичный ключ
     * @return Optional с сущностью или пустой, если не найдено
     */
    Optional<T> findById(K id);

    /**
     * Обновляет существующую сущность.
     * @param entity объект с изменениями
     * @return обновлённая сущность
     */
    T update(T entity);

    /**
     * Удаляет сущность из базы данных.
     * @param entity удаляемый объект
     */
    void delete(T entity);

    /**
     * Возвращает список всех сущностей данного типа.
     * @return список сущностей
     */
    List<T> findAll();

    /**
     * Сохраняет сущность и сразу же выполняет flush в контекст персистентности.
     * @param entity объект для сохранения
     * @return сохранённая сущность
     */
    T saveAndFlush(T entity);
}
