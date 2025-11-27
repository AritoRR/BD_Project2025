package ru.ananev.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
public class Service_ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "cost_our", nullable = false, precision = 10, scale = 2)
    private BigDecimal costOur;

    @Column(name = "cost_foreign", nullable = false, precision = 10, scale = 2)
    private BigDecimal costForeign;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Work> works = new ArrayList<>();

    // Конструкторы
    public Service_() {}

    public Service_(String name, BigDecimal costOur, BigDecimal costForeign) {
        this.name = name;
        this.costOur = costOur;
        this.costForeign = costForeign;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCostOur() {
        return costOur;
    }

    public void setCostOur(BigDecimal costOur) {
        this.costOur = costOur;
    }

    public BigDecimal getCostForeign() {
        return costForeign;
    }

    public void setCostForeign(BigDecimal costForeign) {
        this.costForeign = costForeign;
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", costOur=" + costOur +
                ", costForeign=" + costForeign +
                '}';
    }
}