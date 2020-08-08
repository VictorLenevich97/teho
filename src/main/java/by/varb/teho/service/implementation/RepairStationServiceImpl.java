package by.varb.teho.service.implementation;

import by.varb.teho.entity.RepairStation;
import by.varb.teho.repository.RepairStationRepository;
import by.varb.teho.service.RepairStationService;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Service
public class RepairStationServiceImpl implements RepairStationService {

    private final RepairStationRepository repairStationRepository;

    public RepairStationServiceImpl(RepairStationRepository repairStationRepository) {
        this.repairStationRepository = repairStationRepository;
    }

    @Override
    public List<RepairStation> getRepairStations() {
        return (ArrayList<RepairStation>) this.repairStationRepository.findAll();
    }
}
