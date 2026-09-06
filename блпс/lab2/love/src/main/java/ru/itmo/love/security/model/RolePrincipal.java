package ru.itmo.love.security.model;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;
import java.util.Objects;

/** principal представляющий роль пользователя */
public class RolePrincipal implements Principal, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** имя роли */
    private final String name;

    /** создаёт principal с указанным именем роли */
    public RolePrincipal(String name) {
        this.name = name;
    }

    /** возвращает имя роли */
    @Override
    public String getName() {
        return name;
    }

    /** сравнивает по имени роли */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RolePrincipal that)) {
            return false;
        }
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
