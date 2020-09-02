package va.rit.teho.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface EquipmentInRepairRepository extends CrudRepository<EquipmentInRepair, EquipmentInRepairId>, JpaSpecificationExecutor<EquipmentInRepair> {

    @Query("SELECT new va.rit.teho.entity.EquipmentInRepairData(eir.equipment.equipmentSubType, " +
            "eir.base.fullName, " +
            "eir.equipment.id, " +
            "eir.equipment.name, " +
            "ipt.amount, " +
            "eir.workhoursDistributionInterval.id, " +
            "eir.count, " +
            "eir.avgLaborInput) FROM EquipmentInRepair eir " +
            "INNER JOIN EquipmentLaborInputPerType ipt ON eir.equipment.id = ipt.equipment.id " +
            "WHERE ipt.repairType.id = ?1 AND eir.equipment.equipmentSubType.equipmentType.id IN ?2 " +
            "GROUP BY eir.equipment.equipmentSubType, " +
            "eir.base.fullName, " +
            "eir.equipment.id, " +
            "eir.equipment.name, " +
            "ipt.amount, " +
            "eir.workhoursDistributionInterval.id, " +
            "eir.count, " +
            "eir.avgLaborInput")
    List<EquipmentInRepairData> findAllAsData(Long repairTypeId, List<Long> equipmentTypeIds);

    default Map<EquipmentType, Map<EquipmentSubType, Map<EquipmentInRepairData.CompositeKey, List<EquipmentInRepairData>>>> findAllGrouped(
            Long repairTypeId,
            List<Long> equipmentTypeIds) {
        Map<EquipmentType, Map<EquipmentSubType, Map<EquipmentInRepairData.CompositeKey, List<EquipmentInRepairData>>>> result = new HashMap<>();
        for (EquipmentInRepairData temp : findAllAsData(repairTypeId, equipmentTypeIds)) {
            result
                    .computeIfAbsent(temp.getCompositeKey().getSubType().getEquipmentType(), k -> new HashMap<>())
                    .computeIfAbsent(temp.getCompositeKey().getSubType(), k -> new HashMap<>())
                    .computeIfAbsent(temp.getCompositeKey(), k -> new ArrayList<>())
                    .add(temp);
        }
        return result;
    }


}
