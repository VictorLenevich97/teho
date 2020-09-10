package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.EquipmentType;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentTypeRepository extends CrudRepository<EquipmentType, Long> {

    List<EquipmentType> findByIdIn(List<Long> ids);

    Optional<EquipmentType> findByFullName(String fullName);
}
