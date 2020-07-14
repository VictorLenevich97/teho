package by.varb.teho.repository;

import by.varb.teho.entity.EquipmentPerBase;
import by.varb.teho.entity.EquipmentPerBaseAmount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentPerBaseRepository extends CrudRepository<EquipmentPerBase, EquipmentPerBaseAmount> {
}
