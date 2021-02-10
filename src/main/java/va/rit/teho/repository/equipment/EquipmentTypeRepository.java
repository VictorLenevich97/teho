package va.rit.teho.repository.equipment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentType;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentTypeRepository extends CrudRepository<EquipmentType, Long> {

    @Query("SELECT COALESCE(max(equipmentType.id), 0) FROM EquipmentType equipmentType")
    Long getMaxId();

    Optional<EquipmentType> findByFullName(String fullName);

    List<EquipmentType> findEquipmentTypeByParentTypeIsNull();

    List<EquipmentType> findEquipmentTypeByParentTypeIsNullAndIdIn(List<Long> ids);
}
