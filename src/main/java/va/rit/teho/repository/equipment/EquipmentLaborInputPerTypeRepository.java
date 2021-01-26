package va.rit.teho.repository.equipment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentLaborInputPerType;
import va.rit.teho.entity.equipment.EquipmentLaborInputPerTypePK;

import java.util.Optional;

@Repository
public interface EquipmentLaborInputPerTypeRepository
        extends CrudRepository<EquipmentLaborInputPerType, EquipmentLaborInputPerTypePK> {

    Optional<EquipmentLaborInputPerType> findByEquipmentIdAndRepairTypeId(Long equipmentId, Long repairTypeId);
}
