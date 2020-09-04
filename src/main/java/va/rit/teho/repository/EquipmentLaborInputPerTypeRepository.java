package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.EquipmentLaborInputPerType;
import va.rit.teho.entity.EquipmentLaborInputPerTypeAmount;

@Repository
public interface EquipmentLaborInputPerTypeRepository
        extends CrudRepository<EquipmentLaborInputPerType, EquipmentLaborInputPerTypeAmount> {
}
