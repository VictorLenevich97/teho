package by.varb.teho.service.implementation;

import by.varb.teho.entity.RepairStation;
import by.varb.teho.entity.RepairStationType;
import by.varb.teho.repository.RepairStationRepository;
import by.varb.teho.repository.RepairStationTypeRepository;
import by.varb.teho.service.RepairStationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RepairStationServiceImpl implements RepairStationService {

    private final RepairStationRepository repairStationRepository;
    private final RepairStationTypeRepository repairStationTypeRepository;

    public RepairStationServiceImpl(
            RepairStationRepository repairStationRepository,
            RepairStationTypeRepository repairStationTypeRepository) {
        this.repairStationRepository = repairStationRepository;
        this.repairStationTypeRepository = repairStationTypeRepository;
    }

    @Override
    public List<RepairStation> getAll() {
        return (ArrayList<RepairStation>) this.repairStationRepository.findAll();
    }

    @Override
    public Optional<RepairStation> find(Long repairStationId) {
        return this.repairStationRepository.findById(repairStationId);
    }

    @Override
    public void add(RepairStation repairStation) {
        this.repairStationRepository.save(repairStation);
    }

    @Override
    public void addType(RepairStationType repairStationType) {
        this.repairStationTypeRepository.save(repairStationType);
    }

    @Override
    public List<RepairStationType> getAllTypes() {
        return (ArrayList<RepairStationType>) this.repairStationTypeRepository.findAll();
    }
}
