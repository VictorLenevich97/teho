package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.*;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.model.Pair;
import va.rit.teho.repository.RepairStationEquipmentCapabilitiesRepository;
import va.rit.teho.repository.RepairStationRepository;
import va.rit.teho.repository.RepairStationTypeRepository;
import va.rit.teho.service.BaseService;
import va.rit.teho.service.RepairStationService;

import java.util.ArrayList;
import java.util.List;

@Service
public class RepairStationServiceImpl implements RepairStationService {

    private final RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository;
    private final RepairStationRepository repairStationRepository;
    private final RepairStationTypeRepository repairStationTypeRepository;
    private final BaseService baseService;

    public RepairStationServiceImpl(
            RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository,
            RepairStationRepository repairStationRepository,
            RepairStationTypeRepository repairStationTypeRepository,
            BaseService baseService) {
        this.repairStationEquipmentCapabilitiesRepository = repairStationEquipmentCapabilitiesRepository;
        this.repairStationRepository = repairStationRepository;
        this.repairStationTypeRepository = repairStationTypeRepository;
        this.baseService = baseService;
    }

    @Override
    public List<RepairStation> list() {
        return (ArrayList<RepairStation>) this.repairStationRepository.findAll();
    }

    @Override
    public Pair<RepairStation, List<RepairStationEquipmentStaff>> get(Long repairStationId) {
        return Pair.of(repairStationRepository
                               .findById(repairStationId)
                               .orElseThrow(() -> new NotFoundException("РВО с id = " + repairStationId + " не найден!")),
                       repairStationEquipmentCapabilitiesRepository.findAllByRepairStationId(repairStationId));
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
    public void setEquipmentStaff(Long repairStationId, Long equipmentId, int availableStaff, int totalStaff) {
        RepairStationEquipmentStaff repairStationEquipmentStaff = new RepairStationEquipmentStaff(new EquipmentPerRepairStation(
                repairStationId,
                equipmentId), totalStaff, availableStaff);
        repairStationEquipmentCapabilitiesRepository.save(repairStationEquipmentStaff);
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
