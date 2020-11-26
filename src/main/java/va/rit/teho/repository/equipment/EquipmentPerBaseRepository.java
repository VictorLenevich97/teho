package va.rit.teho.repository.equipment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentPerBase;
import va.rit.teho.entity.equipment.EquipmentPerBasePK;

import java.util.List;


@Repository
public interface EquipmentPerBaseRepository extends CrudRepository<EquipmentPerBase, EquipmentPerBasePK> {

    @Query("SELECT new va.rit.teho.entity.equipment.EquipmentPerBase(epb.equipment, SUM(epb.amount)) " +
            "FROM EquipmentPerBase epb " +
            "WHERE (coalesce(:equipmentIds, null) IS NULL OR epb.equipment.id IN (:equipmentIds)) " +
            "GROUP BY epb.equipment.id")
    List<EquipmentPerBase> findTotal(List<Long> equipmentIds);

    @Query("SELECT epb " +
            "FROM EquipmentPerBase epb " +
            "WHERE epb.base.id = :baseId AND (" +
            "coalesce(:equipmentIds, null) IS NULL OR epb.equipment.id IN (:equipmentIds))")
    List<EquipmentPerBase> findAllByBaseId(Long baseId, List<Long> equipmentIds);
}
