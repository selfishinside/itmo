package ru.itmo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class DragonCave {
    @Column(nullable = false)
    private double depth;

    public DragonCave() {
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }
}
