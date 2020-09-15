package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.RepairStationType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.RepairStationTypeRepository;
import va.rit.teho.service.RepairStationTypeService;

import java.util.List;

@Service
public class RepairStationTypeServiceImpl implements RepairStationTypeService {

    private final RepairStationTypeRepository repairStationTypeRepository;

    public RepairStationTypeServiceImpl(RepairStationTypeRepository repairStationTypeRepository) {
        this.repairStationTypeRepository = repairStationTypeRepository;
    }

    @Override
    public RepairStationType get(Long id) {
        return repairStationTypeRepository.findById(id)
                                          .orElseThrow(() -> new NotFoundException("Тип РВО с id = " + id + " не найден"));
    }

    @Override
    public Long addType(String name, int workingHoursMin, int workingHoursMax) {
        repairStationTypeRepository.findByName(name).ifPresent(rst -> {
            throw new AlreadyExistsException("Тип РВО", "название", name);
        });
        if (workingHoursMax < workingHoursMin) {
            throw new IncorrectParamException("Верхний предел рабочего времени производственником меньше нижнего!");
        }
        RepairStationType repairStationType = new RepairStationType(name, workingHoursMin, workingHoursMax);
        return repairStationTypeRepository.save(repairStationType).getId();
    }

    @Override
    public void updateType(Long id, String name, int workingHoursMin, int workingHoursMax) {
        RepairStationType repairStationType =
                repairStationTypeRepository.findById(id)
                                           .orElseThrow(() -> new NotFoundException("Тип РВО с id = " + id + " не найден!"));
        if (workingHoursMax < workingHoursMin) {
            throw new IncorrectParamException("Верхний предел рабочего времени производственником меньше нижнего!");
        }
        repairStationType.setName(name);
        repairStationType.setWorkingHoursMax(workingHoursMax);
        repairStationType.setWorkingHoursMin(workingHoursMin);
        repairStationTypeRepository.save(repairStationType);
    }

    @Override
    public List<RepairStationType> listTypes() {
        return (List<RepairStationType>) this.repairStationTypeRepository.findAll();
    }
}
