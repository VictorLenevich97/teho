package va.rit.teho.repository.session;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.session.TehoSession;

import java.util.UUID;

@Repository
public interface SessionRepository extends CrudRepository<TehoSession, UUID> {
}
