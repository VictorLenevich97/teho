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

    private final EquipmentPerFormationService equipmentPerFormationService;
    private final RepairFormationUnitService repairFormationUnitService;
    private final RepairCapabilitiesService repairCapabilitiesService;
    private final LaborInputDistributionService laborInputDistributionService;
    private final EquipmentRFUDistributionService equipmentRFUDistributionService;
    private final FormationService formationService;
    private final RepairFormationService repairFormationService;

    public SessionServiceImpl(SessionRepository sessionRepository,
                              EquipmentPerFormationService equipmentPerFormationService,
                              RepairFormationUnitService repairFormationUnitService,
                              RepairCapabilitiesService repairCapabilitiesService,
                              LaborInputDistributionService laborInputDistributionService,
                              EquipmentRFUDistributionService equipmentRFUDistributionService, FormationService formationService, RepairFormationService repairFormationService) {
        this.sessionRepository = sessionRepository;
        this.equipmentPerFormationService = equipmentPerFormationService;
        this.repairFormationUnitService = repairFormationUnitService;
        this.repairCapabilitiesService = repairCapabilitiesService;
        this.laborInputDistributionService = laborInputDistributionService;
        this.equipmentRFUDistributionService = equipmentRFUDistributionService;
        this.formationService = formationService;
        this.repairFormationService = repairFormationService;
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
        List<Formation> formationList = formationService.list(sessionId);
        for (Formation formation : formationList) {
            Formation newFormation;
            if(formation.getParentFormation() == null || formation.getParentFormation().getId() == null) {
                newFormation = formationService.add(newSession, formation.getShortName(), formation.getFullName());
            } else {
                newFormation = formationService.add(newSession, formation.getShortName(), formation.getFullName(), formation.getParentFormation().getId());
            }
            equipmentPerFormationService.copyEquipmentPerFormationWithIntensityData(sessionId, newSession.getId(), formation, newFormation);

            List<RepairFormation> originalRepairFormations = repairFormationService.list(formation.getId());

            for (RepairFormation originalRepairFormation : originalRepairFormations) {
                RepairFormation newRepairFormation =
                        repairFormationService.add(originalRepairFormation.getName(), originalRepairFormation.getRepairFormationType().getId(), newFormation.getId());

                repairFormationUnitService.copyRFUAndStaff(sessionId, newSession.getId(), originalRepairFormation, newRepairFormation);
            }
//            repairCapabilitiesService.calculateAndUpdateRepairCapabilities(newSession.getId());
            laborInputDistributionService.updateLaborInputDistribution(newSession.getId(), null, null);
//            equipmentRFUDistributionService.distribute(newSession.getId(), null, null, null);


        }
        return newSession;
    }

    @Override
    public TehoSession get(UUID sessionId) {
        return sessionRepository
                .findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Сессии с id = '" + sessionId.toString() + "' не существует!"));
    }

    @Override
    @Transactional
    public void delete(UUID sessionId) {
        sessionRepository.deleteById(sessionId);
    }


}
