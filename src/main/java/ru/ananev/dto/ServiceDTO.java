package ru.ananev.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ServiceDTO {
    private Long id;

    @NotBlank(message = "Название услуги обязательно")
    private String name;

    @NotNull(message = "Стоимость для отечественных авто обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Стоимость должна быть больше 0")
    private BigDecimal costOur;

    @NotNull(message = "Стоимость для иномарок обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Стоимость должна быть больше 0")
    private BigDecimal costForeign;

    // Конструкторы
    public ServiceDTO() {}

    public ServiceDTO(Long id, String name, BigDecimal costOur, BigDecimal costForeign) {
        this.id = id;
        this.name = name;
        this.costOur = costOur;
        this.costForeign = costForeign;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getCostOur() { return costOur; }
    public void setCostOur(BigDecimal costOur) { this.costOur = costOur; }

    public BigDecimal getCostForeign() { return costForeign; }
    public void setCostForeign(BigDecimal costForeign) { this.costForeign = costForeign; }
}