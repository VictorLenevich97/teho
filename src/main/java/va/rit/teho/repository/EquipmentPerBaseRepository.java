package va.rit.teho.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.EquipmentPerBase;
import va.rit.teho.entity.EquipmentPerBaseAmount;
import va.rit.teho.model.Pair;

import java.util.List;

@Repository
public interface EquipmentPerBaseRepository extends CrudRepository<EquipmentPerBase, EquipmentPerBaseAmount> {

    List<EquipmentPerBase> findByBaseId(Long baseId);

    List<EquipmentPerBase> findByEquipmentId(Long equipmentId);

    @Query("SELECT new va.rit.teho.model.Pair(epb, elipt.amount) FROM EquipmentPerBase epb " +
            "INNER JOIN EquipmentLaborInputPerType elipt ON epb.equipment.id = elipt.equipment.id " +
            "WHERE elipt.repairType.id = ?1")
    List<Pair<EquipmentPerBase, Integer>> findAllWithLaborInput(Long repairTypeId);


    @Query("SELECT new va.rit.teho.model.Pair(epb, elipt.amount) FROM EquipmentPerBase epb " +
            "INNER JOIN EquipmentLaborInputPerType elipt ON epb.equipment.id = elipt.equipment.id " +
            "WHERE elipt.repairType.id = ?1 AND epb.base.id = ?2 ")
    List<Pair<EquipmentPerBase, Integer>> findAllWithLaborInputAndBase(Long repairTypeId, Long baseId);


    @Query("SELECT new va.rit.teho.model.Pair(epb, elipt.amount) FROM EquipmentPerBase epb " +
            "INNER JOIN EquipmentLaborInputPerType elipt ON epb.equipment.id = elipt.equipment.id " +
            "WHERE elipt.repairType.id = ?1 AND epb.equipment.equipmentSubType.id = ?2")
    List<Pair<EquipmentPerBase, Integer>> findAllWithLaborInputAndEquipmentSubType(Long repairTypeId,
                                                                                   Long equipmentSubTypeId);


    @Query("SELECT new va.rit.teho.model.Pair(epb, elipt.amount) FROM EquipmentPerBase epb " +
            "INNER JOIN EquipmentLaborInputPerType elipt ON epb.equipment.id = elipt.equipment.id " +
            "WHERE elipt.repairType.id = ?1 AND epb.equipment.equipmentSubType.equipmentType.id = ?2")
    List<Pair<EquipmentPerBase, Integer>> findAllWithLaborInputAndEquipmentType(Long repairTypeId,
                                                                                Long equipmentTypeId);
}
