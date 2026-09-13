package ru.itmo.love.security.auth.jaas;

import java.util.Set;

/**
 * неизменяемая запись пользователя загружаемая из xml файла
 * хранит учетные данные и набор ролей пользователя
 *
 * param username имя пользователя логин
 * param password пароль пользователя в открытом виде
 * param roles набор ролей назначенных пользователю
 */
public record XmlUserRecord(String username, String password, Set<String> roles) {
}
