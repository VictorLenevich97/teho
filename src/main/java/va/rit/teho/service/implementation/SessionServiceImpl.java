package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.TehoSession;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.SessionRepository;
import va.rit.teho.service.SessionService;

import java.util.List;
import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<TehoSession> list() {
        return (List<TehoSession>) sessionRepository.findAll();
    }

    @Override
    public UUID create() {
        TehoSession tehoSession = new TehoSession(null);
        return sessionRepository.save(tehoSession).getId();
    }

    @Override
    public TehoSession create(String name) {
        TehoSession tehoSession = new TehoSession(name);
        return sessionRepository.save(tehoSession);
    }

    @Override
    public TehoSession get(UUID sessionId) {
        return sessionRepository
                .findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Сессии с id = '" + sessionId.toString() + "' не существует!"));
    }

    @Override
    public void delete(UUID sessionId) {
        sessionRepository.deleteById(sessionId);
    }


}
