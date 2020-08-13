package by.varb.teho.service.implementation;

import by.varb.teho.entity.*;
import by.varb.teho.repository.CalculatedRepairCapabilitiesPerDayRepository;
import by.varb.teho.repository.RepairStationEquipmentCapabilitiesRepository;
import by.varb.teho.service.RepairCapabilitiesService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RepairCapabilitiesServiceImpl implements RepairCapabilitiesService {

    private final RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository;
    private final CalculatedRepairCapabilitiesPerDayRepository calculatedRepairCapabilitiesPerDayRepository;

    public RepairCapabilitiesServiceImpl(
            RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository,
            CalculatedRepairCapabilitiesPerDayRepository calculatedRepairCapabilitiesPerDayRepository) {
        this.repairStationEquipmentCapabilitiesRepository = repairStationEquipmentCapabilitiesRepository;
        this.calculatedRepairCapabilitiesPerDayRepository = calculatedRepairCapabilitiesPerDayRepository;
    }

    @Override
    public Optional<RepairStationEquipmentStaff> getRepairStationEquipmentStaff(Long equipmentId, Long repairStationId) {
        EquipmentPerRepairStation equipmentPerRepairStation = new EquipmentPerRepairStation(repairStationId, equipmentId);
        return repairStationEquipmentCapabilitiesRepository.findById(equipmentPerRepairStation);
    }

    @Override
    public void saveCalculatedRepairCapabilities(CalculatedRepairCapabilitesPerDay calculatedRepairCapabilitesPerDay) {
        Optional<CalculatedRepairCapabilitesPerDay> updated = updateIfPresent(calculatedRepairCapabilitesPerDay);
        this.calculatedRepairCapabilitiesPerDayRepository.save(updated.orElse(calculatedRepairCapabilitesPerDay));
    }

    private Optional<CalculatedRepairCapabilitesPerDay> updateIfPresent(CalculatedRepairCapabilitesPerDay calculatedRepairCapabilitesPerDay) {
        EquipmentPerRepairStation equipmentPerRepairStation =
                new EquipmentPerRepairStation(
                        calculatedRepairCapabilitesPerDay.getRepairStation().getId(),
                        calculatedRepairCapabilitesPerDay.getEquipment().getId());
        Optional<CalculatedRepairCapabilitesPerDay> oldRepairCapabilitesPerDay =
                this.calculatedRepairCapabilitiesPerDayRepository.findById(equipmentPerRepairStation);
        oldRepairCapabilitesPerDay.ifPresent(rcpd -> rcpd.setCapability(calculatedRepairCapabilitesPerDay.getCapability()));
        return oldRepairCapabilitesPerDay;
    }

    @Override
    public Map<RepairStation, Map<Equipment, CalculatedRepairCapabilitesPerDay>> getTotalCalculatedRepairCapabilities() {
        Map<RepairStation, Map<Equipment, CalculatedRepairCapabilitesPerDay>> result = new HashMap<>();
        for (CalculatedRepairCapabilitesPerDay calculatedRepairCapabilitesPerDay : this.calculatedRepairCapabilitiesPerDayRepository.findAll()) {
            RepairStation repairStation = calculatedRepairCapabilitesPerDay.getRepairStation();
            result.computeIfAbsent(repairStation, rs -> new HashMap<>());
            result.get(repairStation).put(calculatedRepairCapabilitesPerDay.getEquipment(), calculatedRepairCapabilitesPerDay);
        }
        return result;
    }
}
