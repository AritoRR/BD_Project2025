package ru.ananev.repository;

import ru.ananev.entity.Master;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterRepository extends JpaRepository<Master, Long> {

    Optional<Master> findByName(String name);

    List<Master> findByNameContainingIgnoreCase(String name);

    @Query("SELECT COUNT(m) FROM Master m")
    Long countAllMasters();

    // Для проверки ограничения на количество мастеров (не более 10)
    default boolean canAddNewMaster() {
        return countAllMasters() < 10;
    }
}