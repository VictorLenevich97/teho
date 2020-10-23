package va.rit.teho.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.RepairType;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairTypeRepository extends CrudRepository<RepairType, Long> {
    Optional<RepairType> findByName(String name);

    List<RepairType> findAllByRepairableTrue();

    List<RepairType> findAllByRepairableFalse();

}
