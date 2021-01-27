package va.rit.teho.repository.equipment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.Equipment;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends CrudRepository<Equipment, Long> {

    @Query("SELECT COALESCE(max(e.id), 0) FROM Equipment e")
    Long getMaxId();

    @Query("SELECT e from Equipment e WHERE (coalesce(:ids, null) is null or e.id in (:ids)) AND " +
            "(coalesce(:typeIds, null) is null or e.equipmentType.id in (:typeIds)) " +
            "ORDER BY e.equipmentType.id ASC, e.id ASC")
    List<Equipment> findFiltered(List<Long> ids, List<Long> typeIds);

    Optional<Equipment> findByNameIgnoreCase(String name);
}
