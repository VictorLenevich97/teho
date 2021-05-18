package va.rit.teho.service.implementation.session;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.Tree;
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

    private void copyFormationData(UUID sessionId, TehoSession newSession, Tree.Node<Formation> formationNode, Formation newParent) {
        for (Tree.Node<Formation> child : formationNode.getChildren()) {
            Formation newFormation = formationService.add(newSession, child.getData().getShortName(), child.getData().getFullName(), newParent.getId());
            moveToNewFormationAndSession(sessionId, newSession, child.getData(), newFormation);
            copyFormationData(sessionId, newSession, child, newFormation);
        }
    }

    @Transactional
    public TehoSession copySessionData(UUID sessionId, String name) {
        sessionService.get(sessionId); //проверка на существование
        TehoSession newSession = sessionService.create(name);
        List<Tree<Formation>> formationHierarchy = formationService.listHierarchy(sessionId, null);

        for (Tree<Formation> formationTree : formationHierarchy) {
            Tree.Node<Formation> root = formationTree.getRoot();
            Formation newFormation = formationService.add(newSession, root.getData().getShortName(), root.getData().getFullName());
            moveToNewFormationAndSession(sessionId, newSession, root.getData(), newFormation);

            copyFormationData(sessionId, newSession, root, newFormation);
        }

        return newSession;
    }

    private void moveToNewFormationAndSession(UUID sessionId, TehoSession newSession, Formation originalFormation, Formation newFormation) {
        equipmentPerFormationService.copyEquipmentPerFormationWithIntensityData(sessionId, newSession.getId(), originalFormation, newFormation);

        List<RepairFormation> originalRepairFormations = repairFormationService.list(originalFormation.getId());

        for (RepairFormation originalRepairFormation : originalRepairFormations) {
            RepairFormation newRepairFormation =
                    repairFormationService.add(originalRepairFormation.getName(), originalRepairFormation.getRepairFormationType().getId(), newFormation.getId());

            repairFormationUnitService.copyRFUAndStaff(sessionId, newSession.getId(), originalRepairFormation, newRepairFormation);
        }
    }

    private void recalculate(UUID sessionId) {
        repairCapabilitiesService.calculateAndUpdateRepairCapabilities(sessionId);
        laborInputDistributionService.updateLaborInputDistribution(sessionId, null, null);
        equipmentRFUDistributionService.distribute(sessionId, null, null, null);
    }
}
