package va.rit.teho.service.implementation.repairformation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.repairformation.RepairFormationType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairformation.RepairFormationTypeRepository;
import va.rit.teho.service.repairformation.RepairFormationTypeService;

import java.util.List;

@Service
@Transactional
public class RepairFormationTypeServiceImpl implements RepairFormationTypeService {

    private final RepairFormationTypeRepository repairFormationTypeRepository;

    public RepairFormationTypeServiceImpl(RepairFormationTypeRepository repairFormationTypeRepository) {
        this.repairFormationTypeRepository = repairFormationTypeRepository;
    }

    @Override
    public RepairFormationType get(Long id) {
        return repairFormationTypeRepository.findById(id)
                                            .orElseThrow(() -> new NotFoundException("Тип РВО с id = " + id + " не найден"));
    }

    @Override
    public Long addType(String name, int workingHoursMin, int workingHoursMax) {
        repairFormationTypeRepository.findByName(name).ifPresent(rst -> {
            throw new AlreadyExistsException("Тип РВО", "название", name);
        });
        if (workingHoursMax < workingHoursMin) {
            throw new IncorrectParamException("Верхний предел рабочего времени производственником меньше нижнего!");
        }
        long newId = repairFormationTypeRepository.getMaxId() + 1;
        RepairFormationType repairFormationType = new RepairFormationType(newId,
                                                                          name,
                                                                          workingHoursMin,
                                                                          workingHoursMax);

        return repairFormationTypeRepository.save(repairFormationType).getId();
    }

    @Override
    public void updateType(Long id, String name, int workingHoursMin, int workingHoursMax) {
        RepairFormationType repairFormationType =
                repairFormationTypeRepository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException("Тип РВО с id = " + id + " не найден!"));
        if (workingHoursMax < workingHoursMin) {
            throw new IncorrectParamException("Верхний предел рабочего времени производственником меньше нижнего!");
        }
        repairFormationType.setName(name);
        repairFormationType.setWorkingHoursMax(workingHoursMax);
        repairFormationType.setWorkingHoursMin(workingHoursMin);
        repairFormationTypeRepository.save(repairFormationType);
    }

    @Override
    public List<RepairFormationType> listTypes() {
        return (List<RepairFormationType>) this.repairFormationTypeRepository.findAll();
    }
}
