package va.rit.teho.repository.repairformation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairformation.RepairFormationUnit;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairFormationUnitRepository extends PagingAndSortingRepository<RepairFormationUnit, Long> {

    @Query("SELECT COALESCE(max(rfu.id), 0) FROM RepairFormationUnit rfu")
    Long getMaxId();

    Optional<RepairFormationUnit> findByName(String name);

    @Query("SELECT rdu from RepairFormationUnit rdu WHERE (coalesce(:repairFormationUnitIds, null) is null or rdu.id in (:repairFormationUnitIds)) " +
            "ORDER BY rdu.id ASC")
    List<RepairFormationUnit> findSorted(List<Long> repairFormationUnitIds, Pageable pageable);


    @Query("SELECT rdu from RepairFormationUnit rdu WHERE rdu.repairFormation.id = :repairFormationId AND " +
            "(coalesce(:repairFormationUnitIds, null) is null or rdu.id in (:repairFormationUnitIds)) " +
            "ORDER BY rdu.id ASC")
    List<RepairFormationUnit> findSorted(Long repairFormationId, List<Long> repairFormationUnitIds, Pageable pageable);

    Long countByIdIn(List<Long> ids);

}
