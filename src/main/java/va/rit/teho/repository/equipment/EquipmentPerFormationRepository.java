package va.rit.teho.repository.equipment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentPerFormationPK;

import java.util.List;


@Repository
public interface EquipmentPerFormationRepository extends CrudRepository<EquipmentPerFormation, EquipmentPerFormationPK> {

    @Query("SELECT new va.rit.teho.entity.equipment.EquipmentPerFormation(epb.equipment, epb.formation, SUM(epb.amount)) " +
            "FROM EquipmentPerFormation epb " +
            "WHERE (coalesce(:equipmentIds, null) IS NULL OR epb.equipment.id IN (:equipmentIds)) " +
            "GROUP BY epb.formation.id, epb.equipment.id")
    List<EquipmentPerFormation> findTotal(List<Long> equipmentIds);

    @Query("SELECT epb " +
            "FROM EquipmentPerFormation epb " +
            "WHERE epb.formation.id = :formationId AND (" +
            "coalesce(:equipmentIds, null) IS NULL OR epb.equipment.id IN (:equipmentIds))")
    List<EquipmentPerFormation> findAllByFormationId(Long formationId, List<Long> equipmentIds);

}
