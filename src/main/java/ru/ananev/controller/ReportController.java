package ru.ananev.controller;

import ru.ananev.dto.MasterWorkStatsDTO;
import ru.ananev.dto.ReportDTO;
import ru.ananev.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String reportsDashboard(Model model) {
        try {
            // Показываем отчет за последние 30 дней по умолчанию
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30);

            ReportDTO costReport = reportService.getServiceCostReport(startDate, endDate);
            List<MasterWorkStatsDTO> topMasters = reportService.getCurrentMonthTopMasters();

            model.addAttribute("costReport", costReport);
            model.addAttribute("topMasters", topMasters);
            model.addAttribute("currentMonth", LocalDate.now().getMonthValue());
            model.addAttribute("currentYear", LocalDate.now().getYear());

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке отчетов: " + e.getMessage());
            e.printStackTrace(); // Для отладки
        }

        return "reports/dashboard";
    }

    @GetMapping("/cost")
    public String serviceCostReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Model model) {

        try {
            ReportDTO costReport = reportService.getServiceCostReport(startDate, endDate);
            model.addAttribute("costReport", costReport);

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при формировании отчета: " + e.getMessage());
            e.printStackTrace(); // Для отладки
        }

        return "reports/cost-report";
    }

    @GetMapping("/masters")
    public String topMastersReport(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model) {

        try {
            // Если месяц и год не указаны, используем текущие
            if (month == null) {
                month = LocalDate.now().getMonthValue();
            }
            if (year == null) {
                year = LocalDate.now().getYear();
            }

            List<MasterWorkStatsDTO> topMasters = reportService.getTopMastersByMonth(month, year);
            model.addAttribute("topMasters", topMasters);
            model.addAttribute("selectedMonth", month);
            model.addAttribute("selectedYear", year);
            model.addAttribute("currentYear", LocalDate.now().getYear());

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при формировании отчета: " + e.getMessage());
            e.printStackTrace(); // Для отладки
        }

        return "reports/masters-report";
    }
}