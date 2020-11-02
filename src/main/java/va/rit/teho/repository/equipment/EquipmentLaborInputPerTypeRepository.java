package va.rit.teho.repository.equipment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentLaborInputPerType;
import va.rit.teho.entity.equipment.EquipmentLaborInputPerTypePK;

import java.util.List;

@Repository
public interface EquipmentLaborInputPerTypeRepository
        extends CrudRepository<EquipmentLaborInputPerType, EquipmentLaborInputPerTypePK> {

    @Query(value = "select " +
            "(select coalesce(sum(amount), 0) from equipment_labor_input_per_type inner join equipment on equipment_labor_input_per_type.equipment_id = equipment.id " +
            "where repair_type_id = 1 and equipment.name = ?1) as current_repair," +
            "(select coalesce(sum(amount), 0) from equipment_labor_input_per_type inner join equipment on equipment_labor_input_per_type.equipment_id = equipment.id " +
            "where repair_type_id = 2 and equipment.name = ?1) as avr_repair," +
            "(select coalesce(sum(amount), 0) from equipment_labor_input_per_type inner join equipment on equipment_labor_input_per_type.equipment_id = equipment.id" +
            " where repair_type_id = 3 and equipment.name = ?1) as capital_repair," +
            "(select coalesce(sum(amount), 0) from equipment_labor_input_per_type inner join equipment on equipment_labor_input_per_type.equipment_id = equipment.id" +
            " where repair_type_id = 4 and equipment.name = ?1) as irrevocable_loses",
            nativeQuery = true)
    List<List<Object>> findRepairTypesByEquipmentName(String equipmentName);
}
