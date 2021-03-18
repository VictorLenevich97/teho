package va.rit.teho.repository.intensity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.intensity.Operation;

import java.util.Optional;

@Repository
public interface OperationRepository extends CrudRepository<Operation, Long> {

    @Query("SELECT COALESCE(max(op.id), 0) FROM Operation op")
    Long getMaxId();

    Optional<Operation> findByName(String name);

    Optional<Operation> findByActiveIsTrue();
}
