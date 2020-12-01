package va.rit.teho.service.implementation.repairformation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.repairformation.RepairStationType;
import va.rit.teho.repository.repairformation.RepairStationTypeRepository;
import va.rit.teho.service.repairformation.RepairStationService;

import java.util.List;

@Service
public class RepairStationServiceImpl implements RepairStationService {

    private final RepairStationTypeRepository repairStationTypeRepository;

    public RepairStationServiceImpl(RepairStationTypeRepository repairStationTypeRepository) {
        this.repairStationTypeRepository = repairStationTypeRepository;
    }

    @Override
    public List<RepairStationType> listRepairStationTypes() {
        return (List<RepairStationType>) repairStationTypeRepository.findAll();
    }
}
