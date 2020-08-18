package va.rit.teho.repository;

import va.rit.teho.entity.EquipmentLaborInputPerType;
import va.rit.teho.entity.EquipmentLaborInputPerTypeAmount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentLaborInputPerTypeRepository extends CrudRepository<EquipmentLaborInputPerType, EquipmentLaborInputPerTypeAmount> {
}
