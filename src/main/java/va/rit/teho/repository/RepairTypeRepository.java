package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.RepairType;

import java.util.Optional;

@Repository
public interface RepairTypeRepository extends CrudRepository<RepairType, Long> {
    Optional<RepairType> findByName(String name);
}
