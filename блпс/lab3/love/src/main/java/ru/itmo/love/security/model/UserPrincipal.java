package ru.itmo.love.security.model;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;
import java.util.Objects;

/** principal представляющий имя пользователя */
public class UserPrincipal implements Principal, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** имя пользователя */
    private final String name;

    /** создает principal с указанным именем пользователя */
    public UserPrincipal(String name) {
        this.name = name;
    }

    /** возвращает имя пользователя */
    @Override
    public String getName() {
        return name;
    }

    /** сравнивает по имени пользователя */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserPrincipal that)) {
            return false;
        }
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
