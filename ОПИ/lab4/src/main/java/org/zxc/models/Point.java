package org.zxc.models;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Point implements Serializable {
    private static final long serialVersionUID = 123L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float x;
    private float y;
    private float r;

    @Column(name = "hit_time")
    private Date hitTime;

    private boolean hit;

    public Point(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hitTime = new Date();
        this.hit = calculate();
    }

    public boolean calculate() {

        if (x <= 0 && x >= -r/2 && y >= 0 && y <= r) {
            return true;
        }

        // Четверть круга в четвертой четверти
        if (x >= 0 && y <= 0 && (x*x + y*y <= (r/2)*(r/2))) {
            return true;
        }

        // Треугольник (во второй четверти, спускается к -R/2 по Y при x=0)
        // Линия: y = - (x + r) / 2
        if (x <= 0 && x >= -r && y <= 0 && y >= -(x + r)/2) {
            return true;
        }

        // Если ни одно из условий не выполнено
        return false;
    }
}
