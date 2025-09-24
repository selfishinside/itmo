package org.zxc;

import org.junit.*;
import static org.junit.Assert.*;
import org.zxc.models.Point;


public class HitTest {

    //Calc test
    @Test //Прямоугольник
    public void calc_whenRect() {
        Point point1 = new Point(-0.2f, 0.6f, 1);
        Point point2 = new Point(-1, 1, 1);
        assertTrue(point1.calculate());
        assertFalse(point2.calculate());
    }

    @Test //Треугольник
    public void calc_whenTrn() {
        Point point1 = new Point(-0.2f, -0.2f, 1);
        Point point2 = new Point(-1, -1, 1);
        assertTrue(point1.calculate());
        assertFalse(point2.calculate());
    }

    @Test //Круг
    public void calc_whenSect() {
        Point point1 = new Point(0.2f, -0.2f, 1);
        Point point2 = new Point(1, -1, 1);
        assertTrue(point1.calculate());
        assertFalse(point2.calculate());
    }
}

