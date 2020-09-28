package va.rit.teho.service;

import va.rit.teho.entity.TehoSession;

import java.util.List;
import java.util.UUID;

public interface SessionService {

    List<TehoSession> list();

    UUID create();

    UUID create(String name);

    TehoSession get(UUID sessionId);

}
