package by.varb.teho.repository;

import by.varb.teho.entity.Equipment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentRepository extends CrudRepository<Equipment, Long> {
    Optional<Equipment> findByName(String name);
}
