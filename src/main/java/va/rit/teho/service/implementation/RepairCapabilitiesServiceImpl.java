package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.CalculatedRepairCapabilitesPerDay;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.RepairStation;
import va.rit.teho.entity.RepairStationEquipmentStaff;
import va.rit.teho.enums.RepairTypeEnum;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.CalculatedRepairCapabilitiesPerDayRepository;
import va.rit.teho.repository.RepairStationEquipmentCapabilitiesRepository;
import va.rit.teho.service.CalculationService;
import va.rit.teho.service.RepairCapabilitiesService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RepairCapabilitiesServiceImpl implements RepairCapabilitiesService {

    private final RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository;
    private final CalculatedRepairCapabilitiesPerDayRepository calculatedRepairCapabilitiesPerDayRepository;

    private final CalculationService calculationService;

    public RepairCapabilitiesServiceImpl(
            RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository,
            CalculatedRepairCapabilitiesPerDayRepository calculatedRepairCapabilitiesPerDayRepository,
            CalculationService calculationService) {
        this.repairStationEquipmentCapabilitiesRepository = repairStationEquipmentCapabilitiesRepository;
        this.calculatedRepairCapabilitiesPerDayRepository = calculatedRepairCapabilitiesPerDayRepository;
        this.calculationService = calculationService;
    }

    private CalculatedRepairCapabilitesPerDay getCalculatedRepairCapabilitesPerDay(RepairStationEquipmentStaff rsec) {
        int laborInputAmount = rsec
                .getEquipment()
                .getLaborInputPerTypes()
                .stream()
                .filter(lipt -> lipt.getRepairType().getName().equals(RepairTypeEnum.AVG_REPAIR.getName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Отсутствует значение нормативной трудоемкости " +
                                                                 RepairTypeEnum.AVG_REPAIR.getName() +
                                                                 " ремонта для ВВСТ с id = " + rsec.getEquipment()
                                                                                                   .getId()))
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
        calculateAndUpdateRepairCapabilities(
                (List<RepairStationEquipmentStaff>) this.repairStationEquipmentCapabilitiesRepository.findAll());
    }

    private void calculateAndUpdateRepairCapabilities(List<RepairStationEquipmentStaff> repairStationEquipmentStaffList) {
        List<CalculatedRepairCapabilitesPerDay> capabilitesPerDayList = new ArrayList<>();
        for (RepairStationEquipmentStaff repairStationEquipmentStaff : repairStationEquipmentStaffList) {
            capabilitesPerDayList.add(getCalculatedRepairCapabilitesPerDay(repairStationEquipmentStaff));
        }
        calculatedRepairCapabilitiesPerDayRepository.saveAll(capabilitesPerDayList);
    }

    @Override
    public void calculateAndUpdateRepairCapabilitiesPerStation(Long repairStationId) {
        calculateAndUpdateRepairCapabilities(
                repairStationEquipmentCapabilitiesRepository.findAllByRepairStationId(repairStationId));
    }

    private List<Long> nullIfEmpty(List<Long> collection) {
        return collection == null || collection.isEmpty() ? null : collection;
    }

    @Override
    public Map<RepairStation, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            List<Long> repairStationIds,
            List<Long> equipmentIds,
            List<Long> equipmentSubTypeIds,
            List<Long> equipmentTypeIds) {
        Iterable<CalculatedRepairCapabilitesPerDay> calculatedRepairCapabilitesPerDays =
                calculatedRepairCapabilitiesPerDayRepository.findByIds(nullIfEmpty(repairStationIds),
                                                                       nullIfEmpty(equipmentIds),
                                                                       nullIfEmpty(equipmentSubTypeIds),
                                                                       nullIfEmpty(equipmentTypeIds));
        Map<RepairStation, Map<Equipment, Double>> result = new HashMap<>();
        for (CalculatedRepairCapabilitesPerDay calculatedRepairCapabilitesPerDay : calculatedRepairCapabilitesPerDays) {
            RepairStation repairStation = calculatedRepairCapabilitesPerDay.getRepairStation();
            result.computeIfAbsent(repairStation, rs -> new HashMap<>());
            result
                    .get(repairStation)
                    .put(calculatedRepairCapabilitesPerDay.getEquipment(),
                         calculatedRepairCapabilitesPerDay.getCapability());
        }
        return result;
    }
}
