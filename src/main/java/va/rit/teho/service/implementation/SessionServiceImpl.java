package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.TehoSession;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.SessionRepository;
import va.rit.teho.service.LaborInputDistributionService;
import va.rit.teho.service.RepairCapabilitiesService;
import va.rit.teho.service.RepairStationService;
import va.rit.teho.service.SessionService;

import java.util.List;
import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    private final RepairStationService repairStationService;
    private final RepairCapabilitiesService repairCapabilitiesService;
    private final LaborInputDistributionService laborInputDistributionService;

    public SessionServiceImpl(SessionRepository sessionRepository,
                              RepairStationService repairStationService,
                              RepairCapabilitiesService repairCapabilitiesService,
                              LaborInputDistributionService laborInputDistributionService) {
        this.sessionRepository = sessionRepository;
        this.repairStationService = repairStationService;
        this.repairCapabilitiesService = repairCapabilitiesService;
        this.laborInputDistributionService = laborInputDistributionService;
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
    @Transactional
    public TehoSession copy(UUID sessionId, String name) {
        get(sessionId); //проверка на существование
        TehoSession newSession = create(name);
        repairStationService.copyEquipmentStaff(sessionId, newSession.getId());
        repairCapabilitiesService.copyRepairCapabilities(sessionId, newSession.getId());
        laborInputDistributionService.copyLaborInputDistributionData(sessionId, newSession.getId());
        return newSession;
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
