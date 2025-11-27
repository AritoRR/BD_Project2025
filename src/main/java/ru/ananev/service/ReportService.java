package ru.ananev.service;

import ru.ananev.dto.MasterWorkStatsDTO;
import ru.ananev.dto.ReportDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ReportService {

    private final JdbcTemplate jdbcTemplate;

    // Конструктор с JdbcTemplate
    public ReportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Общая стоимость обслуживания отечественных и импортных автомобилей
     * с использованием хранимой процедуры get_service_costs_by_origin
     */
    public ReportDTO getServiceCostReport(LocalDate startDate, LocalDate endDate) {
        // Если даты не указаны, используем разумные значения по умолчанию
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // Вызов хранимой процедуры
        List<ServiceCostResult> results = jdbcTemplate.query(
                "SELECT * FROM get_service_costs_by_origin(?, ?)",
                new Object[]{startDate, endDate},
                new ServiceCostRowMapper()
        );

        BigDecimal domesticTotal = BigDecimal.ZERO;
        BigDecimal foreignTotal = BigDecimal.ZERO;
        BigDecimal overallTotal = BigDecimal.ZERO;

        for (ServiceCostResult result : results) {
            if ("Отечественные".equals(result.carType)) {
                domesticTotal = result.totalCost;
            } else if ("Иномарки".equals(result.carType)) {
                foreignTotal = result.totalCost;
            }
            overallTotal = overallTotal.add(result.totalCost);
        }

        ReportDTO report = new ReportDTO(domesticTotal, foreignTotal, overallTotal);
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        return report;
    }

    /**
     * Топ-5 мастеров по количеству работ для разных автомобилей в заданном месяце
     * с использованием хранимой процедуры get_top_masters_by_month
     */
    public List<MasterWorkStatsDTO> getTopMastersByMonth(Integer month, Integer year) {
        // Создаем дату для передачи в процедуру
        LocalDate targetDate = LocalDate.of(year, month, 1);

        // Вызов хранимой процедуры
        return jdbcTemplate.query(
                "SELECT * FROM get_top_masters_by_month(?)",
                new Object[]{targetDate},
                new MasterStatsRowMapper()
        );
    }

    /**
     * Получить статистику по мастерам за текущий месяц
     */
    public List<MasterWorkStatsDTO> getCurrentMonthTopMasters() {
        LocalDate now = LocalDate.now();
        return getTopMastersByMonth(now.getMonthValue(), now.getYear());
    }

    // Вспомогательные классы для маппинга результатов

    private static class ServiceCostResult {
        String carType;
        Long totalServices;
        BigDecimal totalCost;
    }

    private static class ServiceCostRowMapper implements RowMapper<ServiceCostResult> {
        @Override
        public ServiceCostResult mapRow(ResultSet rs, int rowNum) throws SQLException {
            ServiceCostResult result = new ServiceCostResult();
            result.carType = rs.getString("car_type");
            result.totalServices = rs.getLong("total_services");
            result.totalCost = rs.getBigDecimal("total_cost");
            return result;
        }
    }

    private static class MasterStatsRowMapper implements RowMapper<MasterWorkStatsDTO> {
        @Override
        public MasterWorkStatsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MasterWorkStatsDTO(
                    null, // ID не возвращается процедурой
                    rs.getString("master_name"),
                    rs.getLong("total_works"),
                    rs.getLong("unique_cars_serviced")
            );
        }
    }

    /**
     * Дополнительный метод: статистика по автомобилям через хранимую процедуру
     */
    public List<CarServiceStats> getCarServiceStatistics() {
        return jdbcTemplate.query(
                "SELECT * FROM get_car_service_statistics()",
                new CarStatsRowMapper()
        );
    }

    // Класс для статистики по автомобилям
    public static class CarServiceStats {
        private String carMark;
        private String carNumber;
        private String carType;
        private Long totalServices;
        private BigDecimal totalCost;

        // Геттеры и сеттеры
        public String getCarMark() { return carMark; }
        public void setCarMark(String carMark) { this.carMark = carMark; }
        public String getCarNumber() { return carNumber; }
        public void setCarNumber(String carNumber) { this.carNumber = carNumber; }
        public String getCarType() { return carType; }
        public void setCarType(String carType) { this.carType = carType; }
        public Long getTotalServices() { return totalServices; }
        public void setTotalServices(Long totalServices) { this.totalServices = totalServices; }
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    }

    private static class CarStatsRowMapper implements RowMapper<CarServiceStats> {
        @Override
        public CarServiceStats mapRow(ResultSet rs, int rowNum) throws SQLException {
            CarServiceStats stats = new CarServiceStats();
            stats.setCarMark(rs.getString("car_mark"));
            stats.setCarNumber(rs.getString("car_number"));
            stats.setCarType(rs.getString("car_type"));
            stats.setTotalServices(rs.getLong("total_services"));
            stats.setTotalCost(rs.getBigDecimal("total_cost"));
            return stats;
        }
    }
}