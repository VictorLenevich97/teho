package by.varb.teho.repository;

import by.varb.teho.entity.EquipmentInRepair;
import by.varb.teho.entity.EquipmentInRepairEmbeddable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentInRepairRepository extends CrudRepository<EquipmentInRepair, EquipmentInRepairEmbeddable> {
}
