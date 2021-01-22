package va.rit.teho.repository.common;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.common.Stage;

import java.util.Optional;

@Repository
public interface StageRepository extends CrudRepository<Stage, Long> {

    @Query("SELECT COALESCE(max(s.id), 0) FROM Stage s")
    Long getMaxId();

    Optional<Stage> findByStageNum(int stageNum);
}
