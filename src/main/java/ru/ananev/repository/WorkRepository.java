package ru.ananev.repository;

import ru.ananev.dto.MasterWorkStatsDTO;
import ru.ananev.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    List<Work> findByDateWork(LocalDate dateWork);

    List<Work> findByDateWorkBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(w) FROM Work w WHERE w.master.id = :masterId AND w.dateWork = :dateWork")
    Long countWorksByMasterAndDate(@Param("masterId") Long masterId, @Param("dateWork") LocalDate dateWork);

    List<Work> findAllByOrderByDateWorkDesc();

    // Проверка ограничения: не более 2 работ в день на мастера
    default boolean canAssignWorkToMaster(Long masterId, LocalDate date) {
        return countWorksByMasterAndDate(masterId, date) < 2;
    }

    /**
     * Получить стоимость услуг по типу автомобиля и диапазону дат
     * ИСПРАВЛЕННЫЙ ЗАПРОС - убрал проверки на NULL
     */
    @Query("SELECT w.car.isForeign, SUM(" +
            "CASE WHEN w.car.isForeign = true THEN w.service.costForeign " +
            "ELSE w.service.costOur END) " +
            "FROM Work w " +
            "WHERE w.dateWork BETWEEN :startDate AND :endDate " +
            "GROUP BY w.car.isForeign")
    List<Object[]> findServiceCostByCarTypeAndDateRange(@Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    /**
     * Топ мастеров по количеству работ для разных автомобилей в заданном месяце
     * ИСПРАВЛЕННЫЙ ЗАПРОС - используем правильные функции для дат
     */
    @Query("SELECT NEW ru.ananev.dto.MasterWorkStatsDTO(" +
            "w.master.id, w.master.name, COUNT(w), COUNT(DISTINCT w.car)) " +
            "FROM Work w " +
            "WHERE EXTRACT(MONTH FROM w.dateWork) = :month AND EXTRACT(YEAR FROM w.dateWork) = :year " +
            "GROUP BY w.master.id, w.master.name " +
            "ORDER BY COUNT(DISTINCT w.car) DESC, COUNT(w) DESC")
    List<MasterWorkStatsDTO> findTopMastersByMonth(@Param("month") Integer month,
                                                   @Param("year") Integer year);

    /**
     * Получить минимальную и максимальную даты работ для фильтров
     */
    @Query("SELECT MIN(w.dateWork), MAX(w.dateWork) FROM Work w")
    List<Object[]> findWorkDateRange();
}