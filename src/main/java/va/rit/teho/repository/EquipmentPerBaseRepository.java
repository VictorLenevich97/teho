package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.EquipmentPerBase;
import va.rit.teho.entity.EquipmentPerBaseAmount;

@Repository
public interface EquipmentPerBaseRepository extends CrudRepository<EquipmentPerBase, EquipmentPerBaseAmount> {
}
