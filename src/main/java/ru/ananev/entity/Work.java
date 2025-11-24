package ru.ananev.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "works")
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_work", nullable = false)
    private LocalDate dateWork;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false, foreignKey = @ForeignKey(name = "fk_works_masters"))
    private Master master;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false, foreignKey = @ForeignKey(name = "fk_works_cars"))
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false, foreignKey = @ForeignKey(name = "fk_works_services"))
    private Service service;

    // Конструкторы
    public Work() {}

    public Work(LocalDate dateWork, Master master, Car car, Service service) {
        this.dateWork = dateWork;
        this.master = master;
        this.car = car;
        this.service = service;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateWork() {
        return dateWork;
    }

    public void setDateWork(LocalDate dateWork) {
        this.dateWork = dateWork;
    }

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        this.master = master;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    // Метод для получения стоимости услуги в зависимости от типа автомобиля
    public Double getActualCost() {
        if (car.getIsForeign()) {
            return service.getCostForeign();
        } else {
            return service.getCostOur();
        }
    }

    @Override
    public String toString() {
        return "Work{" +
                "id=" + id +
                ", dateWork=" + dateWork +
                ", master=" + (master != null ? master.getName() : "null") +
                ", car=" + (car != null ? car.getNumber() : "null") +
                ", service=" + (service != null ? service.getName() : "null") +
                '}';
    }
}