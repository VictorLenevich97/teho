package va.rit.teho.service.implementation.session;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.repairformation.RepairFormation;
import va.rit.teho.entity.session.TehoSession;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.labordistribution.EquipmentRFUDistributionService;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;
import va.rit.teho.service.repairformation.RepairCapabilitiesService;
import va.rit.teho.service.repairformation.RepairFormationService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;
import va.rit.teho.service.session.SessionCopyService;
import va.rit.teho.service.session.SessionService;

import java.util.List;
import java.util.UUID;

@Service
public class SessionCopyServiceImpl implements SessionCopyService {

    private final SessionService sessionService;

    private final EquipmentPerFormationService equipmentPerFormationService;
    private final RepairFormationUnitService repairFormationUnitService;
    private final LaborInputDistributionService laborInputDistributionService;
    private final FormationService formationService;
    private final RepairFormationService repairFormationService;
    private final RepairCapabilitiesService repairCapabilitiesService;
    private final EquipmentRFUDistributionService equipmentRFUDistributionService;

    public SessionCopyServiceImpl(SessionService sessionService, EquipmentPerFormationService equipmentPerFormationService, RepairFormationUnitService repairFormationUnitService, LaborInputDistributionService laborInputDistributionService, FormationService formationService, RepairFormationService repairFormationService, RepairCapabilitiesService repairCapabilitiesService, EquipmentRFUDistributionService equipmentRFUDistributionService) {
        this.sessionService = sessionService;
        this.equipmentPerFormationService = equipmentPerFormationService;
        this.repairFormationUnitService = repairFormationUnitService;
        this.laborInputDistributionService = laborInputDistributionService;
        this.formationService = formationService;
        this.repairFormationService = repairFormationService;
        this.repairCapabilitiesService = repairCapabilitiesService;
        this.equipmentRFUDistributionService = equipmentRFUDistributionService;
    }

    @Override
    public TehoSession copy(UUID sessionId, String name) {
        TehoSession newSession = copySessionData(sessionId, name);
        recalculate(newSession.getId());
        return newSession;
    }

    @Transactional
    public TehoSession copySessionData(UUID sessionId, String name) {
        sessionService.get(sessionId); //проверка на существование
        TehoSession newSession = sessionService.create(name);
        List<Formation> formationList = formationService.list(sessionId);
        for (Formation formation : formationList) {
            Formation newFormation;
            if (formation.getParentFormation() == null || formation.getParentFormation().getId() == null) {
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
        }
        return newSession;
    }

    private void recalculate(UUID sessionId) {
        repairCapabilitiesService.calculateAndUpdateRepairCapabilities(sessionId);
        laborInputDistributionService.updateLaborInputDistribution(sessionId, null, null);
        equipmentRFUDistributionService.distribute(sessionId, null, null, null);
    }
}
