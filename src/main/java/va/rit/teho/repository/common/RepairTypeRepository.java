package va.rit.teho.repository.common;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.common.RepairType;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairTypeRepository extends CrudRepository<RepairType, Long> {
    Optional<RepairType> findByFullName(String name);

    List<RepairType> findAllByCalculatable(boolean calculatable);
}
