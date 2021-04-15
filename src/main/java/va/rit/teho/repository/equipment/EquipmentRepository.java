package va.rit.teho.repository.equipment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.Equipment;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends PagingAndSortingRepository<Equipment, Long> {

    @Query("SELECT COALESCE(max(e.id), 0) FROM Equipment e")
    Long getMaxId();

    List<Equipment> findAllByOrderByIdAsc();

    List<Equipment> findByIdIn(List<Long> ids);

    Long countByNameLikeIgnoreCase(String name);

    List<Equipment> findByOrderByEquipmentTypeIdAscIdAsc(Pageable pageable);

    List<Equipment> findByNameLikeIgnoreCaseOrderByEquipmentTypeIdAscIdAsc(String name, Pageable pageable);

    Optional<Equipment> findByNameIgnoreCase(String name);
}
