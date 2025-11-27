package ru.ananev.repository;

import ru.ananev.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByNumber(String number);

    boolean existsByNumber(String number);

    List<Car> findByIsForeign(Boolean isForeign);

    @Query("SELECT c FROM Car c WHERE c.mark LIKE %:mark%")
    List<Car> findByMarkContaining(@Param("mark") String mark);

    @Query("SELECT COUNT(c) FROM Car c WHERE c.isForeign = :isForeign")
    Long countByType(@Param("isForeign") Boolean isForeign);
}