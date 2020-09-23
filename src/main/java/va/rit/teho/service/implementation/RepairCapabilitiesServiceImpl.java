package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.*;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.CalculatedRepairCapabilitiesPerDayRepository;
import va.rit.teho.repository.RepairStationEquipmentCapabilitiesRepository;
import va.rit.teho.repository.RepairTypeRepository;
import va.rit.teho.service.CalculationService;
import va.rit.teho.service.RepairCapabilitiesService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RepairCapabilitiesServiceImpl implements RepairCapabilitiesService {

    private final RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository;
    private final CalculatedRepairCapabilitiesPerDayRepository calculatedRepairCapabilitiesPerDayRepository;
    private final RepairTypeRepository repairTypeRepository;


    private final CalculationService calculationService;

    public RepairCapabilitiesServiceImpl(
            RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository,
            CalculatedRepairCapabilitiesPerDayRepository calculatedRepairCapabilitiesPerDayRepository,
            RepairTypeRepository repairTypeRepository,
            CalculationService calculationService) {
        this.repairStationEquipmentCapabilitiesRepository = repairStationEquipmentCapabilitiesRepository;
        this.calculatedRepairCapabilitiesPerDayRepository = calculatedRepairCapabilitiesPerDayRepository;
        this.repairTypeRepository = repairTypeRepository;
        this.calculationService = calculationService;
    }

    private CalculatedRepairCapabilitesPerDay getCalculatedRepairCapabilitesPerDay(Long repairTypeId,
                                                                                   Equipment equipment,
                                                                                   RepairStationEquipmentStaff rsec) {
        EquipmentLaborInputPerType laborInputPerType = equipment
                .getLaborInputPerTypes()
                .stream()
                .filter(lipt -> lipt.getRepairType().getId().equals(repairTypeId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Отсутствует значение нормативной трудоемкости по типу ремонта с id = " + repairTypeId +
                                " для ВВСТ с id = " + equipment.getId()));
        double calculatedCapabilities = calculationService.calculateRepairCapabilities(
                rsec.getTotalStaff(),
                rsec.getRepairStation().getRepairStationType().getWorkingHoursMax(),
                laborInputPerType.getAmount());
        return new CalculatedRepairCapabilitesPerDay(
                new EquipmentPerRepairStationWithRepairType(rsec.getEquipmentPerRepairStation().getRepairStationId(),
                        equipment.getId(),
                        repairTypeId),
                rsec.getRepairStation(),
                equipment,
                calculatedCapabilities,
                laborInputPerType.getRepairType());
    }

    @Override
    public void calculateAndUpdateRepairCapabilities(Long repairTypeId) {
        calculateAndUpdateRepairCapabilities(
                (List<RepairStationEquipmentStaff>) this.repairStationEquipmentCapabilitiesRepository.findAll(),
                repairTypeId);
    }

    private void calculateAndUpdateRepairCapabilities(List<RepairStationEquipmentStaff> repairStationEquipmentStaffList,
                                                      Long repairTypeId) {
        List<CalculatedRepairCapabilitesPerDay> capabilitesPerDayList =
                repairStationEquipmentStaffList.stream().flatMap(
                        repairStationEquipmentStaff ->
                                repairStationEquipmentStaff
                                        .getEquipmentSubType()
                                        .getEquipmentSet()
                                        .stream()
                                        .map(e -> getCalculatedRepairCapabilitesPerDay(repairTypeId,
                                                e,
                                                repairStationEquipmentStaff)))
                        .collect(Collectors.toList());
        calculatedRepairCapabilitiesPerDayRepository.saveAll(capabilitesPerDayList);
    }

    @Override
    public void calculateAndUpdateRepairCapabilitiesPerStation(Long repairStationId, Long repairTypeId) {
        calculateAndUpdateRepairCapabilities(
                repairStationEquipmentCapabilitiesRepository.findAllByRepairStationId(repairStationId), repairTypeId);
    }

    @Override
    public Map<RepairStation, Map<Equipment, Double>> getCalculatedRepairCapabilities(List<Long> repairStationIds,
                                                                                      List<Long> equipmentIds,
                                                                                      List<Long> equipmentSubTypeIds,
                                                                                      List<Long> equipmentTypeIds) {
        RepairType repairType = StreamSupport.stream(repairTypeRepository.findAll().spliterator(), false)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Типов ремонта не существует!"));
        return internalGetCalculatedRepairCapabilities(repairType.getId(),
                repairStationIds,
                equipmentIds,
                equipmentSubTypeIds,
                equipmentTypeIds);
    }

    private List<Long> nullIfEmpty(List<Long> collection) {
        return collection == null || collection.isEmpty() ? null : collection;
    }

    @Override
    public Map<RepairStation, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            Long repairTypeId,
            List<Long> repairStationIds,
            List<Long> equipmentIds,
            List<Long> equipmentSubTypeIds,
            List<Long> equipmentTypeIds) {
        return internalGetCalculatedRepairCapabilities(repairTypeId, repairStationIds, equipmentIds, equipmentSubTypeIds, equipmentTypeIds);
    }

    @Override
    public Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> getRepairStationEquipmentStaff(List<Long> repairStationIds,
                                                                                                                 List<Long> equipmentTypeIds,
                                                                                                                 List<Long> equipmentSubTypeIds) {
        List<RepairStationEquipmentStaff> equipmentStaffList =
                repairStationEquipmentCapabilitiesRepository.findFiltered(repairStationIds,
                        equipmentTypeIds,
                        equipmentSubTypeIds);

        Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> result =
                new TreeMap<>(Comparator.comparing(RepairStation::getId));
        for (RepairStationEquipmentStaff repairStationEquipmentStaff : equipmentStaffList) {
            RepairStation repairStation = repairStationEquipmentStaff.getRepairStation();
            result.computeIfAbsent(repairStation, rs -> new TreeMap<>(Comparator.comparing(EquipmentSubType::getId)));
            result.get(repairStation).put(repairStationEquipmentStaff.getEquipmentSubType(), repairStationEquipmentStaff);
        }
        return result;
    }

    private Map<RepairStation, Map<Equipment, Double>> internalGetCalculatedRepairCapabilities(Long repairTypeId,
                                                                                               List<Long> repairStationIds,
                                                                                               List<Long> equipmentIds,
                                                                                               List<Long> equipmentSubTypeIds,
                                                                                               List<Long> equipmentTypeIds) {
        Iterable<CalculatedRepairCapabilitesPerDay> calculatedRepairCapabilitesPerDays =
                calculatedRepairCapabilitiesPerDayRepository.findByIds(
                        repairTypeId,
                        nullIfEmpty(repairStationIds),
                        nullIfEmpty(equipmentIds),
                        nullIfEmpty(equipmentSubTypeIds),
                        nullIfEmpty(equipmentTypeIds));
        Map<RepairStation, Map<Equipment, Double>> result = new TreeMap<>(Comparator.comparing(RepairStation::getId));
        for (CalculatedRepairCapabilitesPerDay calculatedRepairCapabilitesPerDay : calculatedRepairCapabilitesPerDays) {
            RepairStation repairStation = calculatedRepairCapabilitesPerDay.getRepairStation();
            result.computeIfAbsent(repairStation, rs -> new TreeMap<>(Comparator.comparing(Equipment::getId)));
            result.get(repairStation).put(calculatedRepairCapabilitesPerDay.getEquipment(), calculatedRepairCapabilitesPerDay.getCapability());
        }
        return result;
    }
}
