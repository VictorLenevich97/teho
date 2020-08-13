package by.varb.teho.repository;

import by.varb.teho.entity.EquipmentInRepair;
import by.varb.teho.entity.EquipmentInRepairId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentInRepairRepository extends CrudRepository<EquipmentInRepair, EquipmentInRepairId> {
    List<EquipmentInRepair> findByBaseId(Long baseId);
}
