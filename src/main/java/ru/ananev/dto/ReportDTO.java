package ru.ananev.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class ReportDTO {

    // Для отчета по стоимости обслуживания
    private BigDecimal domesticTotal;
    private BigDecimal foreignTotal;
    private BigDecimal overallTotal;
    private LocalDate startDate;
    private LocalDate endDate;

    // Для отчета по мастерам
    private Integer month;
    private Integer year;

    // Конструкторы
    public ReportDTO() {
        this.domesticTotal = BigDecimal.ZERO;
        this.foreignTotal = BigDecimal.ZERO;
        this.overallTotal = BigDecimal.ZERO;
    }

    public ReportDTO(BigDecimal domesticTotal, BigDecimal foreignTotal, BigDecimal overallTotal) {
        this.domesticTotal = domesticTotal != null ? domesticTotal : BigDecimal.ZERO;
        this.foreignTotal = foreignTotal != null ? foreignTotal : BigDecimal.ZERO;
        this.overallTotal = overallTotal != null ? overallTotal : BigDecimal.ZERO;
    }

    // Геттеры и сеттеры
    public BigDecimal getDomesticTotal() {
        return domesticTotal != null ? domesticTotal : BigDecimal.ZERO;
    }
    public void setDomesticTotal(BigDecimal domesticTotal) {
        this.domesticTotal = domesticTotal;
    }

    public BigDecimal getForeignTotal() {
        return foreignTotal != null ? foreignTotal : BigDecimal.ZERO;
    }
    public void setForeignTotal(BigDecimal foreignTotal) {
        this.foreignTotal = foreignTotal;
    }

    public BigDecimal getOverallTotal() {
        return overallTotal != null ? overallTotal : BigDecimal.ZERO;
    }
    public void setOverallTotal(BigDecimal overallTotal) {
        this.overallTotal = overallTotal;
    }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    // Методы для расчета процентов
    public BigDecimal getDomesticPercentage() {
        if (getOverallTotal().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getDomesticTotal()
                .divide(getOverallTotal(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    public BigDecimal getForeignPercentage() {
        if (getOverallTotal().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getForeignTotal()
                .divide(getOverallTotal(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    // Метод для проверки наличия данных
    public boolean hasData() {
        return getOverallTotal().compareTo(BigDecimal.ZERO) > 0;
    }
}