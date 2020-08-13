package by.varb.teho.repository;

import by.varb.teho.entity.EquipmentType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentTypeRepository extends CrudRepository<EquipmentType, Long> {
}
