package va.rit.teho.repository.equipment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentType;

import java.util.Optional;

@Repository
public interface EquipmentTypeRepository extends CrudRepository<EquipmentType, Long> {

    Optional<EquipmentType> findByFullName(String fullName);
}
