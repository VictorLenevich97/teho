package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.EquipmentSubType;

@Repository
public interface EquipmentSubTypeRepository extends CrudRepository<EquipmentSubType, Long> {
}
