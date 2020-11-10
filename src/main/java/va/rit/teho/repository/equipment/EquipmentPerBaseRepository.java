package va.rit.teho.repository.equipment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentPerBase;
import va.rit.teho.entity.equipment.EquipmentPerBasePK;

import java.util.List;


@Repository
public interface EquipmentPerBaseRepository extends CrudRepository<EquipmentPerBase, EquipmentPerBasePK> {

    @Query("SELECT new va.rit.teho.entity.equipment.EquipmentPerBase(epb.equipment, SUM(epb.amount)) FROM EquipmentPerBase epb GROUP BY epb.equipment.id")
    List<EquipmentPerBase> findTotal();

    List<EquipmentPerBase> findAllByBaseId(Long baseId);
}
