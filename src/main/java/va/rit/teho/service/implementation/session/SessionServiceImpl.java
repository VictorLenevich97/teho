package va.rit.teho.service.implementation.session;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.repairformation.RepairFormation;
import va.rit.teho.entity.session.TehoSession;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.session.SessionRepository;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.labordistribution.EquipmentRFUDistributionService;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;
import va.rit.teho.service.repairformation.RepairCapabilitiesService;
import va.rit.teho.service.repairformation.RepairFormationService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;
import va.rit.teho.service.session.SessionService;

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
                .orElseThrow(() -> new NotFoundException("Сессии с id = '" + sessionId + "' не существует!"));
    }

    @Override
    @Transactional
    public void delete(UUID sessionId) {
        sessionRepository.deleteById(sessionId);
    }


}
