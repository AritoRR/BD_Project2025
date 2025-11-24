package ru.ananev.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num", unique = true, nullable = false, length = 20)
    private String number;

    @Column(name = "color", nullable = false, length = 50)
    private String color;

    @Column(name = "mark", nullable = false, length = 100)
    private String mark;

    @Column(name = "is_foreign", nullable = false)
    private Boolean isForeign;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Work> works = new ArrayList<>();

    // Конструкторы
    public Car() {}

    public Car(String number, String color, String mark, Boolean isForeign) {
        this.number = number;
        this.color = color;
        this.mark = mark;
        this.isForeign = isForeign;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Boolean getIsForeign() {
        return isForeign;
    }

    public void setIsForeign(Boolean isForeign) {
        this.isForeign = isForeign;
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", color='" + color + '\'' +
                ", mark='" + mark + '\'' +
                ", isForeign=" + isForeign +
                '}';
    }
}