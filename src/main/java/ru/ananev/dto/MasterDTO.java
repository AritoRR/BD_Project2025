package ru.ananev.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class MasterDTO {
    private Long id;

    @NotBlank(message = "Имя мастера обязательно")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String name;

    // Конструкторы
    public MasterDTO() {}

    public MasterDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}