package va.rit.teho.service.implementation.repairformation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.repairformation.RepairFormationType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairformation.RepairFormationUnitTypeRepository;
import va.rit.teho.service.repairformation.RepairFormationUnitTypeService;

import java.util.List;

@Service
@Transactional
public class RepairFormationUnitTypeServiceImpl implements RepairFormationUnitTypeService {

    private final RepairFormationUnitTypeRepository repairFormationUnitTypeRepository;

    public RepairFormationUnitTypeServiceImpl(RepairFormationUnitTypeRepository repairFormationUnitTypeRepository) {
        this.repairFormationUnitTypeRepository = repairFormationUnitTypeRepository;
    }

    @Override
    public RepairFormationType get(Long id) {
        return repairFormationUnitTypeRepository.findById(id)
                                                .orElseThrow(() -> new NotFoundException("Тип РВО с id = " + id + " не найден"));
    }

    @Override
    public Long addType(String name, int workingHoursMin, int workingHoursMax) {
        repairFormationUnitTypeRepository.findByName(name).ifPresent(rst -> {
            throw new AlreadyExistsException("Тип РВО", "название", name);
        });
        if (workingHoursMax < workingHoursMin) {
            throw new IncorrectParamException("Верхний предел рабочего времени производственником меньше нижнего!");
        }
        RepairFormationType repairFormationType = new RepairFormationType(name,
                                                                          workingHoursMin,
                                                                          workingHoursMax);
        return repairFormationUnitTypeRepository.save(repairFormationType).getId();
    }

    @Override
    public void updateType(Long id, String name, int workingHoursMin, int workingHoursMax) {
        RepairFormationType repairFormationType =
                repairFormationUnitTypeRepository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException("Тип РВО с id = " + id + " не найден!"));
        if (workingHoursMax < workingHoursMin) {
            throw new IncorrectParamException("Верхний предел рабочего времени производственником меньше нижнего!");
        }
        repairFormationType.setName(name);
        repairFormationType.setWorkingHoursMax(workingHoursMax);
        repairFormationType.setWorkingHoursMin(workingHoursMin);
        repairFormationUnitTypeRepository.save(repairFormationType);
    }

    @Override
    public List<RepairFormationType> listTypes() {
        return (List<RepairFormationType>) this.repairFormationUnitTypeRepository.findAll();
    }
}
