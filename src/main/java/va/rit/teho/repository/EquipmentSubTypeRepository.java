package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.EquipmentSubType;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentSubTypeRepository extends CrudRepository<EquipmentSubType, Long> {

    List<EquipmentSubType> findByEquipmentTypeId(Long typeId);

    Optional<EquipmentSubType> findByFullName(String fullName);

    List<EquipmentSubType> findByEquipmentTypeIdIn(List<Long> typeIds);

    List<EquipmentSubType> findByIdInAndEquipmentTypeIdIn(List<Long> ids, List<Long> typeIds);

}
