package ru.itmo.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Dragon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Имя дракона не может быть пустым")
    @Column(nullable = false)
    private String name;

    @Valid
    @NotNull(message = "Координаты обязательны")
    @Embedded
    private Coordinates coordinates;

    @NotNull
    @Column(nullable = false, updatable = false)
    private ZonedDateTime creationDate;

    @Valid
    @NotNull(message = "Пещера обязательна")
    @Embedded
    private DragonCave cave;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Person killer;

    @Positive(message = "Возраст должен быть больше 0")
    @Column(nullable = false)
    private long age;

    @Enumerated(EnumType.STRING)
    private Color color;

    @NotNull(message = "Тип дракона обязателен")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DragonType type;

    @NotNull(message = "Характер дракона обязателен")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DragonCharacter character;

    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "size", column = @Column(name = "head_size", nullable = false)),
            @AttributeOverride(name = "eyesCount", column = @Column(name = "head_eyes_count")),
            @AttributeOverride(name = "toothCount", column = @Column(name = "head_tooth_count", nullable = false))
    })
    private DragonHead head;

    public Dragon() {
    }

    @PrePersist
    protected void onCreate() {
        creationDate = ZonedDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public DragonCave getCave() {
        return cave;
    }

    public void setCave(DragonCave cave) {
        this.cave = cave;
    }

    public Person getKiller() {
        return killer;
    }

    public void setKiller(Person killer) {
        this.killer = killer;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public DragonType getType() {
        return type;
    }

    public void setType(DragonType type) {
        this.type = type;
    }

    public DragonCharacter getCharacter() {
        return character;
    }

    public void setCharacter(DragonCharacter character) {
        this.character = character;
    }

    public DragonHead getHead() {
        return head;
    }

    public void setHead(DragonHead head) {
        this.head = head;
    }

    public String getHeadKey() {
        return head == null ? "Без головы" : head.groupKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dragon dragon = (Dragon) o;
        return id != 0 && id == dragon.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
