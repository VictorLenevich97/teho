package va.rit.teho.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.*;

import java.util.*;

@Repository
public interface EquipmentInRepairRepository
        extends CrudRepository<EquipmentInRepair, EquipmentInRepairId>, JpaSpecificationExecutor<EquipmentInRepair> {

    List<EquipmentInRepair> findByTehoSessionId(UUID sessionId);

    @Query("SELECT new va.rit.teho.entity.EquipmentInRepairData(eir.equipment.equipmentSubType, " +
            "eir.base.fullName, " +
            "eir.equipment.id, " +
            "eir.equipment.name, " +
            "ipt.amount, " +
            "eir.workhoursDistributionInterval.id, " +
            "eir.count, " +
            "eir.avgLaborInput) FROM EquipmentInRepair eir " +
            "INNER JOIN EquipmentLaborInputPerType ipt ON eir.equipment.id = ipt.equipment.id " +
            "WHERE ipt.repairType.id = :repairTypeId AND eir.tehoSession.id = :sessionId AND" +
            "(coalesce(:equipmentTypeIds, null) is null or eir.equipment.equipmentSubType.equipmentType.id IN (:equipmentTypeIds)) " +
            "GROUP BY eir.equipment.equipmentSubType, " +
            "eir.base.fullName, " +
            "eir.equipment.id, " +
            "eir.equipment.name, " +
            "ipt.amount, " +
            "eir.workhoursDistributionInterval.id, " +
            "eir.count, " +
            "eir.avgLaborInput")
    List<EquipmentInRepairData> findAllAsData(UUID sessionId, Long repairTypeId, List<Long> equipmentTypeIds);

    default Map<EquipmentType, Map<EquipmentSubType, Map<EquipmentInRepairData.CompositeKey, List<EquipmentInRepairData>>>> findAllGrouped(
            UUID sessionId,
            Long repairTypeId,
            List<Long> equipmentTypeIds) {
        Map<EquipmentType, Map<EquipmentSubType, Map<EquipmentInRepairData.CompositeKey, List<EquipmentInRepairData>>>> result = new HashMap<>();
        for (EquipmentInRepairData temp : findAllAsData(sessionId, repairTypeId, equipmentTypeIds)) {
            result
                    .computeIfAbsent(temp.getCompositeKey().getSubType().getEquipmentType(), k -> new HashMap<>())
                    .computeIfAbsent(temp.getCompositeKey().getSubType(), k -> new HashMap<>())
                    .computeIfAbsent(temp.getCompositeKey(), k -> new ArrayList<>())
                    .add(temp);
        }
        return result;
    }


}
