package ru.ananev.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class WorkDTO {
    private Long id;

    @NotNull(message = "Дата работы обязательна")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateWork;

    @NotNull(message = "Мастер обязателен")
    private Long masterId;

    @NotNull(message = "Автомобиль обязателен")
    private Long carId;

    @NotNull(message = "Услуга обязательна")
    private Long serviceId;

    // Дополнительные поля для отображения
    private String masterName;
    private String carNumber;
    private String carMark;
    private String serviceName;
    private Double actualCost;

    // Конструкторы
    public WorkDTO() {}

    public WorkDTO(Long id, LocalDate dateWork, Long masterId, Long carId, Long serviceId) {
        this.id = id;
        this.dateWork = dateWork;
        this.masterId = masterId;
        this.carId = carId;
        this.serviceId = serviceId;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDateWork() { return dateWork; }
    public void setDateWork(LocalDate dateWork) { this.dateWork = dateWork; }

    public Long getMasterId() { return masterId; }
    public void setMasterId(Long masterId) { this.masterId = masterId; }

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getMasterName() { return masterName; }
    public void setMasterName(String masterName) { this.masterName = masterName; }

    public String getCarNumber() { return carNumber; }
    public void setCarNumber(String carNumber) { this.carNumber = carNumber; }

    public String getCarMark() { return carMark; }
    public void setCarMark(String carMark) { this.carMark = carMark; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public Double getActualCost() { return actualCost; }
    public void setActualCost(Double actualCost) { this.actualCost = actualCost; }
}