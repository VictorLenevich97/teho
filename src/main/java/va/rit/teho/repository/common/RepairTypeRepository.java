package va.rit.teho.repository.common;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.common.RepairType;

import java.util.List;

@Repository
public interface RepairTypeRepository extends CrudRepository<RepairType, Long> {
    List<RepairType> findByCalculatableOrderByIdAsc(boolean calculatable);
}
