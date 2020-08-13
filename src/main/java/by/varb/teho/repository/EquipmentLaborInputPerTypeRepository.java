package by.varb.teho.repository;

import by.varb.teho.entity.EquipmentLaborInputPerType;
import by.varb.teho.entity.EquipmentLaborInputPerTypeAmount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentLaborInputPerTypeRepository extends CrudRepository<EquipmentLaborInputPerType, EquipmentLaborInputPerTypeAmount> {
}
