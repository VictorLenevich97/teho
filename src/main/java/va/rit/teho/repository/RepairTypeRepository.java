package va.rit.teho.repository;

import va.rit.teho.entity.RepairType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairTypeRepository extends CrudRepository<RepairType, Long> {
}
