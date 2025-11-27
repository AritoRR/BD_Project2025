package ru.ananev.repository;


import ru.ananev.entity.Work;
import ru.ananev.entity.Master;
import ru.ananev.entity.Car;
import ru.ananev.entity.Service;
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

    List<Work> findByMaster(Master master);

    List<Work> findByCar(Car car);

    List<Work> findByService(Service service);

    List<Work> findByMasterAndDateWork(Master master, LocalDate dateWork);

    @Query("SELECT COUNT(w) FROM Work w WHERE w.master = :master AND w.dateWork = :dateWork")
    Long countWorksByMasterAndDate(@Param("master") Master master, @Param("dateWork") LocalDate dateWork);

    @Query("SELECT w FROM Work w WHERE w.dateWork >= :startDate ORDER BY w.dateWork DESC")
    List<Work> findRecentWorks(@Param("startDate") LocalDate startDate);

    // Для проверки ограничения: не более 1 работы в день на мастера
    default boolean canAssignWorkToMaster(Master master, LocalDate date) {
        return countWorksByMasterAndDate(master, date) < 2;
    }

    // Получение работ за последний месяц
    default List<Work> findWorksFromLastMonth() {
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        return findByDateWorkAfter(oneMonthAgo);
    }

    List<Work> findByDateWorkAfter(LocalDate date);
}