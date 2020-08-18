package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.*;
import va.rit.teho.enums.RepairTypeEnum;
import va.rit.teho.exception.RepairTypeLaborInputNotFoundException;
import va.rit.teho.repository.CalculatedRepairCapabilitiesPerDayRepository;
import va.rit.teho.repository.RepairStationEquipmentCapabilitiesRepository;
import va.rit.teho.service.CalculationService;
import va.rit.teho.service.EquipmentService;
import va.rit.teho.service.RepairCapabilitiesService;
import va.rit.teho.service.RepairStationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RepairCapabilitiesServiceImpl implements RepairCapabilitiesService {

    private final RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository;
    private final CalculatedRepairCapabilitiesPerDayRepository calculatedRepairCapabilitiesPerDayRepository;

    private final EquipmentService equipmentService;
    private final RepairStationService repairStationService;
    private final CalculationService calculationService;

    public RepairCapabilitiesServiceImpl(
            RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository,
            CalculatedRepairCapabilitiesPerDayRepository calculatedRepairCapabilitiesPerDayRepository,
            EquipmentService equipmentService,
            RepairStationService repairStationService,
            CalculationService calculationService) {
        this.repairStationEquipmentCapabilitiesRepository = repairStationEquipmentCapabilitiesRepository;
        this.calculatedRepairCapabilitiesPerDayRepository = calculatedRepairCapabilitiesPerDayRepository;
        this.equipmentService = equipmentService;
        this.repairStationService = repairStationService;
        this.calculationService = calculationService;
    }

    private CalculatedRepairCapabilitesPerDay getCalculatedRepairCapabilitesPerDay(RepairStationEquipmentStaff rsec) {
        int laborInputAmount = rsec
                .getEquipment()
                .getLaborInputPerTypes()
                .stream()
                .filter(lipt -> lipt.getRepairType().getName().equals(RepairTypeEnum.AVG_REPAIR.getName()))
                .findFirst()
                .orElseThrow(() -> new RepairTypeLaborInputNotFoundException(RepairTypeEnum.AVG_REPAIR,
                                                                             rsec.getEquipment()))
                .getAmount();
        double calculatedCapabilities = calculationService.calculateRepairCapabilities(
                rsec.getTotalStaff(),
                rsec.getRepairStation().getRepairStationType().getWorkingHoursMax(),
                laborInputAmount);
        return new CalculatedRepairCapabilitesPerDay(rsec.getEquipmentPerRepairStation(),
                                                     rsec.getRepairStation(),
                                                     rsec.getEquipment(),
                                                     calculatedCapabilities);
    }

    @Override
    public void calculateAndUpdateRepairCapabilities() {
        List<Equipment> equipmentList = this.equipmentService.list();
        List<RepairStation> repairStations = this.repairStationService.list();
        List<EquipmentPerRepairStation> equipmentPerRepairStations =
                equipmentList
                        .stream()
                        .flatMap(e -> repairStations
                                .stream()
                                .map(rs -> new EquipmentPerRepairStation(rs.getId(), e.getId())))
                        .collect(Collectors.toList());
        Iterable<RepairStationEquipmentStaff> repairStationEquipmentStaffList =
                repairStationEquipmentCapabilitiesRepository.findAllById(equipmentPerRepairStations);
        List<CalculatedRepairCapabilitesPerDay> capabilitesPerDayList = new ArrayList<>();
        for (RepairStationEquipmentStaff repairStationEquipmentStaff : repairStationEquipmentStaffList) {
            capabilitesPerDayList.add(getCalculatedRepairCapabilitesPerDay(repairStationEquipmentStaff));
        }
        calculatedRepairCapabilitiesPerDayRepository.saveAll(capabilitesPerDayList);
    }

    @Override
    public Map<RepairStation, Map<Equipment, CalculatedRepairCapabilitesPerDay>> getTotalCalculatedRepairCapabilities() {
        Map<RepairStation, Map<Equipment, CalculatedRepairCapabilitesPerDay>> result = new HashMap<>();
        for (CalculatedRepairCapabilitesPerDay calculatedRepairCapabilitesPerDay :
                this.calculatedRepairCapabilitiesPerDayRepository.findAll()) {
            RepairStation repairStation = calculatedRepairCapabilitesPerDay.getRepairStation();
            result.computeIfAbsent(repairStation, rs -> new HashMap<>());
            result
                    .get(repairStation)
                    .put(calculatedRepairCapabilitesPerDay.getEquipment(), calculatedRepairCapabilitesPerDay);
        }
        return result;
    }
}
