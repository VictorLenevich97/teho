package va.rit.teho.service.implementation.common;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.repository.common.RepairTypeRepository;
import va.rit.teho.service.common.RepairTypeService;

import java.util.List;

@Service
@Transactional
public class RepairTypeServiceImpl implements RepairTypeService {

    private final RepairTypeRepository repairTypeRepository;

    public RepairTypeServiceImpl(RepairTypeRepository repairTypeRepository) {
        this.repairTypeRepository = repairTypeRepository;
    }

    @Override
    public List<RepairType> list(boolean calculatable) {
        return repairTypeRepository.findAllByCalculatable(calculatable);
    }

    @Override
    public List<RepairType> list() {
        return (List<RepairType>) repairTypeRepository.findAll();
    }
}
