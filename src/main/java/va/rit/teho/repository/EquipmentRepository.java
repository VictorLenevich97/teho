package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public interface EquipmentRepository extends CrudRepository<Equipment, Long> {

    default Map<EquipmentType, List<Equipment>> getEquipmentGroupedByType() {
        return StreamSupport
                .stream(findAll().spliterator(), false)
                .collect(Collectors.groupingBy(e -> e.getEquipmentSubType().getEquipmentType()));
    }

    Optional<Equipment> findByName(String name);
}
