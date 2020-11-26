package va.rit.teho.repository.repairdivision;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairdivision.RepairDivisionUnit;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairDivisionUnitRepository extends PagingAndSortingRepository<RepairDivisionUnit, Long> {

    Optional<RepairDivisionUnit> findByName(String name);

    @Query("SELECT rdu from RepairDivisionUnit rdu WHERE (coalesce(:repairDivisionUnitIds, null) is null or rdu.id in (:repairDivisionUnitIds)) " +
            "ORDER BY rdu.id ASC")
    List<RepairDivisionUnit> findSorted(List<Long> repairDivisionUnitIds, Pageable pageable);

}
