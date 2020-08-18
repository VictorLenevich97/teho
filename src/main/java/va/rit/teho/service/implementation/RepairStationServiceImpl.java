package va.rit.teho.service.implementation;

import va.rit.teho.entity.Base;
import va.rit.teho.entity.RepairStation;
import va.rit.teho.entity.RepairStationType;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.RepairStationRepository;
import va.rit.teho.repository.RepairStationTypeRepository;
import va.rit.teho.service.BaseService;
import va.rit.teho.service.RepairStationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RepairStationServiceImpl implements RepairStationService {

    private final RepairStationRepository repairStationRepository;
    private final RepairStationTypeRepository repairStationTypeRepository;
    private final BaseService baseService;

    public RepairStationServiceImpl(
            RepairStationRepository repairStationRepository,
            RepairStationTypeRepository repairStationTypeRepository, BaseService baseService) {
        this.repairStationRepository = repairStationRepository;
        this.repairStationTypeRepository = repairStationTypeRepository;
        this.baseService = baseService;
    }

    @Override
    public List<RepairStation> list() {
        return (ArrayList<RepairStation>) this.repairStationRepository.findAll();
    }

    @Override
    public Optional<RepairStation> find(Long repairStationId) {
        return this.repairStationRepository.findById(repairStationId);
    }

    @Override
    public Long add(String name, Long baseId, Long typeId, int amount) {
        Base base = baseService.get(baseId);
        RepairStationType repairStationType =
                repairStationTypeRepository
                        .findById(typeId)
                        .orElseThrow(() -> new NotFoundException("Тип РВО с id = " + typeId + " не найден"));
        RepairStation repairStation = new RepairStation(name, repairStationType, base, amount);
        return repairStation.getId();
    }

    @Override
    public Long addType(String name, int workingHoursMin, int workingHoursMax) {
        RepairStationType repairStationType = new RepairStationType(name, workingHoursMin, workingHoursMax);
        return repairStationTypeRepository.save(repairStationType).getId();
    }

    @Override
    public List<RepairStationType> listTypes() {
        return (ArrayList<RepairStationType>) this.repairStationTypeRepository.findAll();
    }
}
