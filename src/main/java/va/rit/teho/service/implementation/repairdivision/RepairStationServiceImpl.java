package va.rit.teho.service.implementation.repairdivision;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.repairdivision.RepairStationType;
import va.rit.teho.repository.repairdivision.RepairStationTypeRepository;
import va.rit.teho.service.repairdivision.RepairStationService;

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
