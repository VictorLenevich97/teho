package va.rit.teho.service.implementation.repairformation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.repairformation.RepairFormation;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairformation.RepairFormationRepository;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.repairformation.RepairFormationService;
import va.rit.teho.service.repairformation.RepairFormationTypeService;

import java.util.List;
import java.util.UUID;

@Service
public class RepairFormationServiceImpl implements RepairFormationService {

    private final RepairFormationRepository repairFormationRepository;

    private final RepairFormationTypeService repairFormationTypeService;
    private final FormationService formationService;

    public RepairFormationServiceImpl(RepairFormationRepository repairFormationRepository,
                                      RepairFormationTypeService repairFormationTypeService,
                                      FormationService formationService) {
        this.repairFormationRepository = repairFormationRepository;
        this.repairFormationTypeService = repairFormationTypeService;
        this.formationService = formationService;
    }

    @Override
    public List<RepairFormation> list(UUID sessionId) {
        return repairFormationRepository.findAllByFormationTehoSessionId(sessionId);
    }

    @Override
    public List<RepairFormation> list(Long formationId) {
        return repairFormationRepository.findAllByFormationId(formationId);
    }

    @Override
    public RepairFormation get(Long id) {
        return repairFormationRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "Ремонтное формирование не найдено!"));
    }

    @Override
    public RepairFormation add(String name, Long typeId, Long formationId) {
        Long newId = repairFormationRepository.getMaxId() + 1;
        RepairFormation repairFormation = new RepairFormation(newId,
                                                              name,
                                                              formationService.get(formationId),
                                                              repairFormationTypeService.get(typeId));
        return repairFormationRepository.save(repairFormation);
    }

    @Override
    public RepairFormation update(Long id, String name, Long typeId, Long formationId) {
        RepairFormation repairFormation = repairFormationRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Формирование не найдено!"));
        repairFormation.setName(name);
        repairFormation.setRepairFormationType(repairFormationTypeService.get(typeId));
        repairFormation.setFormation(formationService.get(formationId));
        return repairFormationRepository.save(repairFormation);
    }

    @Override
    public void delete(Long id) {
        repairFormationRepository.deleteById(id);
    }
}
