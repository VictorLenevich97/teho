package va.rit.teho.service.session;

import va.rit.teho.entity.session.TehoSession;

import java.util.UUID;

public interface SessionCopyService {
    TehoSession copy(UUID sessionId, String name);
}
