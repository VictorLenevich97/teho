package by.varb.teho.repository;

import by.varb.teho.entity.EquipmentInRepair;
import by.varb.teho.entity.EquipmentInRepairId;
import by.varb.teho.model.Pair;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public interface EquipmentInRepairRepository extends CrudRepository<EquipmentInRepair, EquipmentInRepairId> {
    List<EquipmentInRepair> findByBaseId(Long baseId);

    default Map<Pair<Long, Long>, List<EquipmentInRepair>> findAllGroupedByBaseAndEquipment() {
        return StreamSupport
                .stream(findAll().spliterator(), false)
                .collect(Collectors.groupingBy(eir -> Pair.of(eir.getBase().getId(), eir.getEquipment().getId())));
    }
}
