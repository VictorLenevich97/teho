package va.rit.teho.service.session;

import va.rit.teho.entity.session.TehoSession;

import java.util.List;
import java.util.UUID;

public interface SessionService {

    List<TehoSession> list();

    UUID create();

    TehoSession create(String name);

    TehoSession copy(UUID sessionId, String name);

    TehoSession get(UUID sessionId);

    void delete(UUID sessionId);

}
