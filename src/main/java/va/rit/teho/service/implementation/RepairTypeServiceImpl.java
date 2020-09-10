package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.RepairType;
import va.rit.teho.repository.RepairTypeRepository;
import va.rit.teho.service.RepairTypeService;

import java.util.List;

@Service
public class RepairTypeServiceImpl implements RepairTypeService {

    private final RepairTypeRepository repairTypeRepository;

    public RepairTypeServiceImpl(RepairTypeRepository repairTypeRepository) {
        this.repairTypeRepository = repairTypeRepository;
    }

    @Override
    public List<RepairType> list(boolean repairable) {
        List<RepairType> result;
        if (repairable) {
            result = repairTypeRepository.findAllByRepairableTrue();
        } else {
            result = repairTypeRepository.findAllByRepairableFalse();
        }
        return result;
    }

    @Override
    public List<RepairType> list() {
        return (List<RepairType>) repairTypeRepository.findAll();
    }
}
