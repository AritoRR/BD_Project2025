package ru.ananev.repository;

import ru.ananev.entity.Service_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service_, Long> {

    Optional<Service_> findByName(String name);

    List<Service_> findByNameContainingIgnoreCase(String name);

    @Query("SELECT s FROM Service_ s WHERE s.costOur BETWEEN :minCost AND :maxCost OR s.costForeign BETWEEN :minCost AND :maxCost")
    List<Service_> findByCostRange(@Param("minCost") Double minCost, @Param("maxCost") Double maxCost);
}