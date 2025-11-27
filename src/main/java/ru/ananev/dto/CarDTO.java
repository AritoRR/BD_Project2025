package ru.ananev.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CarDTO {
    private Long id;

    @NotBlank(message = "Номер автомобиля обязателен")
    @Pattern(regexp = "^[А-ЯA-Z0-9]+$", message = "Номер должен содержать только заглавные буквы и цифры")
    private String number;

    @NotBlank(message = "Цвет обязателен")
    private String color;

    @NotBlank(message = "Марка обязательна")
    private String mark;

    @NotNull(message = "Тип автомобиля обязателен")
    private Boolean isForeign;

    // Конструкторы
    public CarDTO() {}

    public CarDTO(Long id, String number, String color, String mark, Boolean isForeign) {
        this.id = id;
        this.number = number;
        this.color = color;
        this.mark = mark;
        this.isForeign = isForeign;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getMark() { return mark; }
    public void setMark(String mark) { this.mark = mark; }

    public Boolean getIsForeign() { return isForeign; }
    public void setIsForeign(Boolean isForeign) { this.isForeign = isForeign; }
}