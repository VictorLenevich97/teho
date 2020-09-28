package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.RestorationType;
import va.rit.teho.entity.TehoSession;

import java.util.UUID;

@Repository
public interface SessionRepository extends CrudRepository<TehoSession, UUID> {
}
