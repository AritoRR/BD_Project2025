package ru.ananev.service;

import ru.ananev.dto.MasterWorkStatsDTO;
import ru.ananev.dto.ReportDTO;
import ru.ananev.repository.WorkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportService {

    private final WorkRepository workRepository;

    public ReportService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    /**
     * Общая стоимость обслуживания отечественных и импортных автомобилей
     * с фильтрацией по датам
     */
    public ReportDTO getServiceCostReport(LocalDate startDate, LocalDate endDate) {
        // Если даты не указаны, используем разумные значения по умолчанию
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<Object[]> results = workRepository.findServiceCostByCarTypeAndDateRange(startDate, endDate);

        BigDecimal domesticTotal = BigDecimal.ZERO;
        BigDecimal foreignTotal = BigDecimal.ZERO;
        BigDecimal overallTotal = BigDecimal.ZERO;

        for (Object[] result : results) {
            Boolean isForeign = (Boolean) result[0];
            BigDecimal cost = (BigDecimal) result[1];

            if (isForeign != null && cost != null) {
                if (isForeign) {
                    foreignTotal = foreignTotal.add(cost);
                } else {
                    domesticTotal = domesticTotal.add(cost);
                }
                overallTotal = overallTotal.add(cost);
            }
        }

        ReportDTO report = new ReportDTO(domesticTotal, foreignTotal, overallTotal);
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        return report;
    }

    /**
     * Топ-5 мастеров по количеству работ для разных автомобилей в заданном месяце
     */
    public List<MasterWorkStatsDTO> getTopMastersByMonth(Integer month, Integer year) {
        List<MasterWorkStatsDTO> allMasters = workRepository.findTopMastersByMonth(month, year);
        // Ограничиваем до 5 записей в Java коде
        return allMasters.stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Получить статистику по мастерам за текущий месяц
     */
    public List<MasterWorkStatsDTO> getCurrentMonthTopMasters() {
        LocalDate now = LocalDate.now();
        return getTopMastersByMonth(now.getMonthValue(), now.getYear());
    }

    /**
     * Получить диапазон дат работ
     */
    public LocalDate[] getWorkDateRange() {
        List<Object[]> result = workRepository.findWorkDateRange();
        if (result != null && !result.isEmpty() && result.get(0)[0] != null) {
            Object[] dates = result.get(0);
            return new LocalDate[]{(LocalDate) dates[0], (LocalDate) dates[1]};
        }
        return new LocalDate[]{LocalDate.now().minusMonths(1), LocalDate.now()};
    }
}