package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public interface EquipmentSubTypeRepository extends CrudRepository<EquipmentSubType, Long> {

    default Map<EquipmentType, List<EquipmentSubType>> findAllGroupedByType() {
        return StreamSupport
                .stream(findAll().spliterator(), false)
                .collect(Collectors.groupingBy(EquipmentSubType::getEquipmentType));
    }

    List<EquipmentSubType> findByEquipmentTypeId(Long typeId);

}
