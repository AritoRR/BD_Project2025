package ru.ananev.dto;

public class MasterWorkStatsDTO {
    private Long masterId;
    private String masterName;
    private Long workCount;
    private Long uniqueCarsCount;

    // Конструкторы
    public MasterWorkStatsDTO() {}

    public MasterWorkStatsDTO(Long masterId, String masterName, Long workCount, Long uniqueCarsCount) {
        this.masterId = masterId;
        this.masterName = masterName;
        this.workCount = workCount;
        this.uniqueCarsCount = uniqueCarsCount;
    }

    // Геттеры и сеттеры
    public Long getMasterId() { return masterId; }
    public void setMasterId(Long masterId) { this.masterId = masterId; }

    public String getMasterName() { return masterName; }
    public void setMasterName(String masterName) { this.masterName = masterName; }

    public Long getWorkCount() { return workCount; }
    public void setWorkCount(Long workCount) { this.workCount = workCount; }

    public Long getUniqueCarsCount() { return uniqueCarsCount; }
    public void setUniqueCarsCount(Long uniqueCarsCount) { this.uniqueCarsCount = uniqueCarsCount; }
}