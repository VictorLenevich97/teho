package va.rit.teho.service.implementation.repairformation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.repairformation.RepairFormation;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairformation.RepairFormationRepository;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.repairformation.RepairFormationService;
import va.rit.teho.service.repairformation.RepairFormationTypeService;

import java.util.List;

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
    public List<RepairFormation> list() {
        return (List<RepairFormation>) repairFormationRepository.findAll();
    }

    @Override
    public List<RepairFormation> list(Long formationId) {
        return repairFormationRepository.findAllByFormationId(formationId);
    }

    @Override
    public Long add(String name, Long typeId, Long formationId) {
        RepairFormation repairFormation = new RepairFormation(name,
                                                              formationService.get(formationId),
                                                              repairFormationTypeService.get(typeId));
        RepairFormation saved = repairFormationRepository.save(repairFormation);
        return saved.getId();
    }

    @Override
    public Long update(Long id, String name, Long typeId, Long formationId) {
        RepairFormation repairFormation = repairFormationRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Формирование не найдено!"));
        RepairFormation updated = new RepairFormation(name,
                                                      formationService.get(formationId),
                                                      repairFormationTypeService.get(typeId));
        updated.setId(repairFormation.getId());
        repairFormationRepository.save(updated);
        return updated.getId();
    }
}
