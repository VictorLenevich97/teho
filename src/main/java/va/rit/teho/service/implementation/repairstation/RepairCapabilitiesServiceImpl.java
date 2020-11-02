package va.rit.teho.service.implementation.repairstation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentLaborInputPerType;
import va.rit.teho.entity.repairstation.RepairStationRepairCapability;
import va.rit.teho.entity.repairstation.RepairStationRepairCapabilityPK;
import va.rit.teho.entity.repairstation.RepairStation;
import va.rit.teho.entity.repairstation.RepairStationEquipmentStaff;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairstation.RepairStationRepairCapabilityRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.repairstation.RepairCapabilitiesService;
import va.rit.teho.service.repairstation.RepairStationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class RepairCapabilitiesServiceImpl implements RepairCapabilitiesService {

    private final RepairStationRepairCapabilityRepository calculatedRepairCapabilitiesPerDayRepository;

    private final CalculationService calculationService;
    private final RepairStationService repairStationService;

    public RepairCapabilitiesServiceImpl(
            RepairStationRepairCapabilityRepository calculatedRepairCapabilitiesPerDayRepository,
            CalculationService calculationService,
            RepairStationService repairStationService) {
        this.calculatedRepairCapabilitiesPerDayRepository = calculatedRepairCapabilitiesPerDayRepository;
        this.calculationService = calculationService;
        this.repairStationService = repairStationService;
    }

    private Function<Equipment, RepairStationRepairCapability> getCalculatedRepairCapabilitesPerDay(UUID sessionId,
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

            RepairStationRepairCapabilityPK stationWithRepairType =
                    new RepairStationRepairCapabilityPK(
                            rsec.getEquipmentPerRepairStation().getRepairStationId(),
                            equipment.getId(),
                            repairTypeId,
                            sessionId);
            return new RepairStationRepairCapability(stationWithRepairType, calculatedCapabilities);
        };
    }

    @Override
    public void copyRepairCapabilities(UUID originalSessionId, UUID newSessionId) {
        List<RepairStationRepairCapability> repairCapabilities =
                calculatedRepairCapabilitiesPerDayRepository.findByIds(originalSessionId, null, null, null, null, null);

        List<RepairStationRepairCapability> updatedRepairCapabilitesPerDayList =
                repairCapabilities.stream().map(crcpd -> crcpd.copy(newSessionId)).collect(Collectors.toList());

        calculatedRepairCapabilitiesPerDayRepository.saveAll(updatedRepairCapabilitesPerDayList);
    }

    @Override
    public void calculateAndUpdateRepairCapabilities(UUID sessionId, Long repairTypeId) {
        List<RepairStationEquipmentStaff> repairStationStaff = repairStationService.listRepairStationEquipmentStaff();
        calculateAndUpdateRepairCapabilities(sessionId, repairStationStaff, repairTypeId);
    }

    private void calculateAndUpdateRepairCapabilities(UUID sessionId,
                                                      List<RepairStationEquipmentStaff> repairStationEquipmentStaffList,
                                                      Long repairTypeId) {
        List<RepairStationRepairCapability> capabilitesPerDayList =
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
                repairStationService.listRepairStationEquipmentStaff(repairStationId);

        calculateAndUpdateRepairCapabilities(sessionId, repairStationStaff, repairTypeId);
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
        Iterable<RepairStationRepairCapability> calculatedRepairCapabilitesPerDays =
                calculatedRepairCapabilitiesPerDayRepository.findByIds(
                        sessionId,
                        repairTypeId,
                        repairStationIds,
                        equipmentIds,
                        equipmentSubTypeIds,
                        equipmentTypeIds);
        Map<RepairStation, Map<Equipment, Double>> result = new HashMap<>();
        for (RepairStationRepairCapability calculatedRepairCapabilitesPerDay : calculatedRepairCapabilitesPerDays) {
            RepairStation repairStation = calculatedRepairCapabilitesPerDay.getRepairStation();
            result.computeIfAbsent(repairStation, rs -> new HashMap<>());
            result.get(repairStation).put(calculatedRepairCapabilitesPerDay.getEquipment(),
                                          calculatedRepairCapabilitesPerDay.getCapability());
        }
        return result;
    }
}
