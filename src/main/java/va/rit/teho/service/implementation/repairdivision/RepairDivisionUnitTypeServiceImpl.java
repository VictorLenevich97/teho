package va.rit.teho.service.implementation.repairdivision;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairdivision.RepairDivisionUnitTypeRepository;
import va.rit.teho.service.repairdivision.RepairDivisionUnitTypeService;

import java.util.List;

@Service
@Transactional
public class RepairDivisionUnitTypeServiceImpl implements RepairDivisionUnitTypeService {

    private final RepairDivisionUnitTypeRepository repairDivisionUnitTypeRepository;

    public RepairDivisionUnitTypeServiceImpl(RepairDivisionUnitTypeRepository repairDivisionUnitTypeRepository) {
        this.repairDivisionUnitTypeRepository = repairDivisionUnitTypeRepository;
    }

    @Override
    public RepairDivisionUnitType get(Long id) {
        return repairDivisionUnitTypeRepository.findById(id)
                                               .orElseThrow(() -> new NotFoundException("Тип РВО с id = " + id + " не найден"));
    }

    @Override
    public Long addType(String name, int workingHoursMin, int workingHoursMax) {
        repairDivisionUnitTypeRepository.findByName(name).ifPresent(rst -> {
            throw new AlreadyExistsException("Тип РВО", "название", name);
        });
        if (workingHoursMax < workingHoursMin) {
            throw new IncorrectParamException("Верхний предел рабочего времени производственником меньше нижнего!");
        }
        RepairDivisionUnitType repairDivisionUnitType = new RepairDivisionUnitType(name,
                                                                                   workingHoursMin,
                                                                                   workingHoursMax);
        return repairDivisionUnitTypeRepository.save(repairDivisionUnitType).getId();
    }

    @Override
    public void updateType(Long id, String name, int workingHoursMin, int workingHoursMax) {
        RepairDivisionUnitType repairDivisionUnitType =
                repairDivisionUnitTypeRepository.findById(id)
                                                .orElseThrow(() -> new NotFoundException("Тип РВО с id = " + id + " не найден!"));
        if (workingHoursMax < workingHoursMin) {
            throw new IncorrectParamException("Верхний предел рабочего времени производственником меньше нижнего!");
        }
        repairDivisionUnitType.setName(name);
        repairDivisionUnitType.setWorkingHoursMax(workingHoursMax);
        repairDivisionUnitType.setWorkingHoursMin(workingHoursMin);
        repairDivisionUnitTypeRepository.save(repairDivisionUnitType);
    }

    @Override
    public List<RepairDivisionUnitType> listTypes() {
        return (List<RepairDivisionUnitType>) this.repairDivisionUnitTypeRepository.findAll();
    }
}
