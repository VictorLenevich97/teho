package va.rit.teho.service.implementation.repairformation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.repairformation.RepairStationType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairformation.RepairStationTypeRepository;
import va.rit.teho.service.repairformation.RepairStationService;

import java.util.List;
import java.util.Optional;

@Service
public class RepairStationServiceImpl implements RepairStationService {

    private final RepairStationTypeRepository repairStationTypeRepository;

    public RepairStationServiceImpl(RepairStationTypeRepository repairStationTypeRepository) {
        this.repairStationTypeRepository = repairStationTypeRepository;
    }

    @Override
    public List<RepairStationType> listTypes() {
        return (List<RepairStationType>) repairStationTypeRepository.findAll();
    }

    @Override
    public RepairStationType addType(String name) {
        Optional<RepairStationType> existingRepairStationType = repairStationTypeRepository.findByNameIgnoreCase(name);
        existingRepairStationType.ifPresent(rst -> {
            throw new AlreadyExistsException("Тип мастерской", "название", name);
        });
        long newId = repairStationTypeRepository.getMaxId() + 1;
        return repairStationTypeRepository.save(new RepairStationType(newId, name));
    }

    @Override
    public RepairStationType updateType(Long id, String name) {
        Optional<RepairStationType> repairStationType = repairStationTypeRepository.findById(id);
        if (!repairStationType.isPresent()) {
            throw new NotFoundException("Тип мастерской (id = " + id + ") не найден!");
        }
        Optional<RepairStationType> existing = repairStationTypeRepository.findByNameIgnoreCase(name);
        existing.ifPresent(rst -> {
            if (!rst.getId().equals(id)) {
                throw new AlreadyExistsException("Тип мастерской", "название", name);
            }
        });
        return repairStationTypeRepository.save(new RepairStationType(id, name));
    }

}
