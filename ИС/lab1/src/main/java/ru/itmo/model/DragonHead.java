package ru.itmo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class DragonHead {
    @Column(nullable = false)
    private long size;

    private Long eyesCount;

    @Column(nullable = false)
    private long toothCount;

    public DragonHead() {
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Long getEyesCount() {
        return eyesCount;
    }

    public void setEyesCount(Long eyesCount) {
        this.eyesCount = eyesCount;
    }

    public long getToothCount() {
        return toothCount;
    }

    public void setToothCount(long toothCount) {
        this.toothCount = toothCount;
    }

    public String groupKey() {
        return "size=" + size + ", eyes=" + (eyesCount == null ? "null" : eyesCount) + ", teeth=" + toothCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DragonHead that = (DragonHead) o;
        return size == that.size && toothCount == that.toothCount && Objects.equals(eyesCount, that.eyesCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, eyesCount, toothCount);
    }
}
