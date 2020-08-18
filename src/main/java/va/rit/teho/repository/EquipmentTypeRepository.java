package va.rit.teho.repository;

import va.rit.teho.entity.EquipmentType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentTypeRepository extends CrudRepository<EquipmentType, Long> {

    Optional<EquipmentType> findByFullName(String fullName);
}
