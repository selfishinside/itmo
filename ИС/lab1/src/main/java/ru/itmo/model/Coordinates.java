package ru.itmo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Coordinates {
    @Max(value = 188, message = "x не должен быть больше 188")
    @Column(nullable = false)
    private long x;

    @NotNull(message = "y не может быть null")
    @Min(value = -706, message = "y должен быть больше -707")
    @Column(nullable = false)
    private Long y;

    public Coordinates() {
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }
}
