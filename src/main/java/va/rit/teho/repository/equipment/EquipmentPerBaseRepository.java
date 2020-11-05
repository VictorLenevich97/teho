package va.rit.teho.repository.equipment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentPerBase;
import va.rit.teho.entity.equipment.EquipmentPerBasePK;

import java.util.List;


@Repository
public interface EquipmentPerBaseRepository extends CrudRepository<EquipmentPerBase, EquipmentPerBasePK> {
    List<EquipmentPerBase> findAllByBaseId(Long baseId);
}
