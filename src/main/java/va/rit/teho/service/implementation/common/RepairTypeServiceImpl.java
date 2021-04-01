package va.rit.teho.service.implementation.common;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.exception.NotFoundException;
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
        return repairTypeRepository.findByCalculatableOrderByIdAsc(calculatable);
    }

    @Override
    public List<RepairType> list() {
        return (List<RepairType>) repairTypeRepository.findAll();
    }

    @Override
    public RepairType switchCalculatableFlag(Long id) {
        RepairType repairType = repairTypeRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "Тип ремонта с id = " + id + " не существует!"));
        repairType.setCalculatable(!repairType.isCalculatable());
        return repairTypeRepository.save(repairType);
    }
}
