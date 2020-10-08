package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.*;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.CalculatedRepairCapabilitiesPerDayRepository;
import va.rit.teho.repository.RepairStationEquipmentStaffRepository;
import va.rit.teho.repository.RepairTypeRepository;
import va.rit.teho.service.CalculationService;
import va.rit.teho.service.RepairCapabilitiesService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RepairCapabilitiesServiceImpl implements RepairCapabilitiesService {

    private final RepairStationEquipmentStaffRepository repairStationEquipmentStaffRepository;
    private final CalculatedRepairCapabilitiesPerDayRepository calculatedRepairCapabilitiesPerDayRepository;
    private final RepairTypeRepository repairTypeRepository;


    private final CalculationService calculationService;

    public RepairCapabilitiesServiceImpl(
            RepairStationEquipmentStaffRepository repairStationEquipmentStaffRepository,
            CalculatedRepairCapabilitiesPerDayRepository calculatedRepairCapabilitiesPerDayRepository,
            RepairTypeRepository repairTypeRepository,
            CalculationService calculationService) {
        this.repairStationEquipmentStaffRepository = repairStationEquipmentStaffRepository;
        this.calculatedRepairCapabilitiesPerDayRepository = calculatedRepairCapabilitiesPerDayRepository;
        this.repairTypeRepository = repairTypeRepository;
        this.calculationService = calculationService;
    }

    private Function<Equipment, CalculatedRepairCapabilitesPerDay> getCalculatedRepairCapabilitesPerDay(UUID sessionId,
                                                                                                        Long repairTypeId,
                                                                                                        RepairStationEquipmentStaff rsec) {
        return (equipment) -> {
            EquipmentLaborInputPerType laborInputPerType = equipment
                    .getLaborInputPerTypes()
                    .stream()
                    .filter(lipt -> lipt.getRepairType().getId().equals(repairTypeId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(
                            "Отсутствует значение нормативной трудоемкости по типу ремонта с id = " + repairTypeId +
                                    " для ВВСТ с id = " + equipment.getId()));
            double calculatedCapabilities =
                    calculationService.calculateRepairCapabilities(
                            rsec.getTotalStaff(),
                            rsec.getRepairStation().getRepairStationType().getWorkingHoursMax(),
                            laborInputPerType.getAmount());

            EquipmentPerRepairStationWithRepairType stationWithRepairType =
                    new EquipmentPerRepairStationWithRepairType(
                            rsec.getEquipmentPerRepairStation().getRepairStationId(),
                            equipment.getId(),
                            repairTypeId,
                            sessionId);
            return new CalculatedRepairCapabilitesPerDay(stationWithRepairType, calculatedCapabilities);
        };
    }

    @Override
    public void copyRepairCapabilities(UUID originalSessionId, UUID newSessionId) {
        List<CalculatedRepairCapabilitesPerDay> repairCapabilities =
                calculatedRepairCapabilitiesPerDayRepository.findByIds(originalSessionId, null, null, null, null, null);

        List<CalculatedRepairCapabilitesPerDay> updatedRepairCapabilitesPerDayList =
                repairCapabilities.stream().map(crcpd -> crcpd.copy(newSessionId)).collect(Collectors.toList());

        calculatedRepairCapabilitiesPerDayRepository.saveAll(updatedRepairCapabilitesPerDayList);
    }

    @Override
    public void calculateAndUpdateRepairCapabilities(UUID sessionId, Long repairTypeId) {
        List<RepairStationEquipmentStaff> repairStationStaff =
                (List<RepairStationEquipmentStaff>) this.repairStationEquipmentStaffRepository.findAll();
        calculateAndUpdateRepairCapabilities(sessionId, repairStationStaff, repairTypeId);
    }

    private void calculateAndUpdateRepairCapabilities(UUID sessionId,
                                                      List<RepairStationEquipmentStaff> repairStationEquipmentStaffList,
                                                      Long repairTypeId) {
        List<CalculatedRepairCapabilitesPerDay> capabilitesPerDayList =
                repairStationEquipmentStaffList.stream().flatMap(
                        repairStationEquipmentStaff ->
                                repairStationEquipmentStaff
                                        .getEquipmentSubType()
                                        .getEquipmentSet()
                                        .stream()
                                        .map(getCalculatedRepairCapabilitesPerDay(sessionId,
                                                                                  repairTypeId,
                                                                                  repairStationEquipmentStaff)))
                        .collect(Collectors.toList());
        calculatedRepairCapabilitiesPerDayRepository.saveAll(capabilitesPerDayList);
    }

    @Override
    public void calculateAndUpdateRepairCapabilitiesPerStation(UUID sessionId,
                                                               Long repairStationId,
                                                               Long repairTypeId) {
        List<RepairStationEquipmentStaff> repairStationStaff =
                repairStationEquipmentStaffRepository.findAllByRepairStationId(repairStationId);

        calculateAndUpdateRepairCapabilities(sessionId, repairStationStaff, repairTypeId);
    }

    @Override
    public Map<RepairStation, Map<Equipment, Double>> getCalculatedRepairCapabilities(UUID sessionId,
                                                                                      List<Long> repairStationIds,
                                                                                      List<Long> equipmentIds,
                                                                                      List<Long> equipmentSubTypeIds,
                                                                                      List<Long> equipmentTypeIds) {
        RepairType repairType = StreamSupport.stream(repairTypeRepository.findAll().spliterator(), false)
                                             .findFirst()
                                             .orElseThrow(() -> new NotFoundException("Типов ремонта не существует!"));
        return internalGetCalculatedRepairCapabilities(
                sessionId, repairType.getId(), repairStationIds, equipmentIds, equipmentSubTypeIds, equipmentTypeIds);
    }

    @Override
    public Map<RepairStation, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            UUID sessionId,
            Long repairTypeId,
            List<Long> repairStationIds,
            List<Long> equipmentIds,
            List<Long> equipmentSubTypeIds,
            List<Long> equipmentTypeIds) {
        return internalGetCalculatedRepairCapabilities(
                sessionId, repairTypeId, repairStationIds, equipmentIds, equipmentSubTypeIds, equipmentTypeIds);
    }



    private Map<RepairStation, Map<Equipment, Double>> internalGetCalculatedRepairCapabilities(UUID sessionId,
                                                                                               Long repairTypeId,
                                                                                               List<Long> repairStationIds,
                                                                                               List<Long> equipmentIds,
                                                                                               List<Long> equipmentSubTypeIds,
                                                                                               List<Long> equipmentTypeIds) {
        Iterable<CalculatedRepairCapabilitesPerDay> calculatedRepairCapabilitesPerDays =
                calculatedRepairCapabilitiesPerDayRepository.findByIds(
                        sessionId,
                        repairTypeId,
                        repairStationIds,
                        equipmentIds,
                        equipmentSubTypeIds,
                        equipmentTypeIds);
        Map<RepairStation, Map<Equipment, Double>> result = new HashMap<>();
        for (CalculatedRepairCapabilitesPerDay calculatedRepairCapabilitesPerDay : calculatedRepairCapabilitesPerDays) {
            RepairStation repairStation = calculatedRepairCapabilitesPerDay.getRepairStation();
            result.computeIfAbsent(repairStation, rs -> new HashMap<>());
            result.get(repairStation).put(calculatedRepairCapabilitesPerDay.getEquipment(),
                                          calculatedRepairCapabilitesPerDay.getCapability());
        }
        return result;
    }
}
