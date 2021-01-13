package va.rit.teho.service.implementation.repairformation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentLaborInputPerType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapability;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapabilityPK;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairformation.RepairFormationUnitRepairCapabilityRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.repairformation.RepairCapabilitiesService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class RepairCapabilitiesServiceImpl implements RepairCapabilitiesService {

    private final RepairFormationUnitRepairCapabilityRepository calculatedRepairCapabilitiesPerDayRepository;

    private final CalculationService calculationService;
    private final RepairFormationUnitService repairFormationUnitService;

    public RepairCapabilitiesServiceImpl(
            RepairFormationUnitRepairCapabilityRepository calculatedRepairCapabilitiesPerDayRepository,
            CalculationService calculationService,
            RepairFormationUnitService repairFormationUnitService) {
        this.calculatedRepairCapabilitiesPerDayRepository = calculatedRepairCapabilitiesPerDayRepository;
        this.calculationService = calculationService;
        this.repairFormationUnitService = repairFormationUnitService;
    }

    private Function<Equipment, RepairFormationUnitRepairCapability> getCalculatedRepairCapabilitesPerDay(UUID sessionId,
                                                                                                          Long repairTypeId,
                                                                                                          RepairFormationUnitEquipmentStaff rsec) {
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
                            rsec.getTotalStaff() * rsec.getRepairFormationUnit().getStationAmount(),
                            rsec
                                    .getRepairFormationUnit()
                                    .getRepairFormation()
                                    .getRepairFormationType()
                                    .getWorkingHoursMax(),
                            laborInputPerType.getAmount());

            RepairFormationUnitRepairCapabilityPK stationWithRepairType =
                    new RepairFormationUnitRepairCapabilityPK(
                            rsec.getEquipmentPerRepairFormationUnit().getRepairFormationUnitId(),
                            equipment.getId(),
                            repairTypeId,
                            sessionId);
            return new RepairFormationUnitRepairCapability(stationWithRepairType, calculatedCapabilities);
        };
    }

    @Override
    public void copyRepairCapabilities(UUID originalSessionId, UUID newSessionId) {
        List<RepairFormationUnitRepairCapability> repairCapabilities =
                calculatedRepairCapabilitiesPerDayRepository.findByIds(originalSessionId, null, null, null, null, null);

        List<RepairFormationUnitRepairCapability> updatedRepairCapabilitesPerDayList =
                repairCapabilities.stream().map(crcpd -> crcpd.copy(newSessionId)).collect(Collectors.toList());

        calculatedRepairCapabilitiesPerDayRepository.saveAll(updatedRepairCapabilitesPerDayList);
    }

    @Override
    public void calculateAndUpdateRepairCapabilities(UUID sessionId, Long repairTypeId) {
        List<RepairFormationUnitEquipmentStaff> repairFormationStaff = repairFormationUnitService.listEquipmentStaff(
                sessionId);
        calculateAndUpdateRepairCapabilities(sessionId, repairFormationStaff, repairTypeId);
    }

    private void calculateAndUpdateRepairCapabilities(UUID sessionId,
                                                      List<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffList,
                                                      Long repairTypeId) {
        List<RepairFormationUnitRepairCapability> capabilitesPerDayList =
                repairFormationUnitEquipmentStaffList.stream().flatMap(
                        repairFormationEquipmentStaff ->
                                repairFormationEquipmentStaff
                                        .getEquipmentSubType()
                                        .getEquipmentSet()
                                        .stream()
                                        .map(getCalculatedRepairCapabilitesPerDay(sessionId,
                                                                                  repairTypeId,
                                                                                  repairFormationEquipmentStaff)))
                                                     .collect(Collectors.toList());
        calculatedRepairCapabilitiesPerDayRepository.saveAll(capabilitesPerDayList);
    }

    @Override
    public void calculateAndUpdateRepairCapabilitiesPerStation(UUID sessionId,
                                                               Long repairFormationUnitId,
                                                               Long repairTypeId) {
        List<RepairFormationUnitEquipmentStaff> repairFormationUnitStaff =
                repairFormationUnitService.listEquipmentStaff(repairFormationUnitId, sessionId);

        calculateAndUpdateRepairCapabilities(sessionId, repairFormationUnitStaff, repairTypeId);
    }

    @Override
    public void updateRepairCapabilities(UUID sessionId,
                                         Long repairFormationUnitId,
                                         Long repairTypeId,
                                         Map<Long, Double> capabilitiesMap) {
        List<RepairFormationUnitRepairCapability> repairFormationUnitRepairCapabilities =
                capabilitiesMap.entrySet().stream().map(
                        equipmentIdCapabilityEntry -> {
                            Long equipmentId = equipmentIdCapabilityEntry.getKey();
                            Double capability = equipmentIdCapabilityEntry.getValue();

                            return new RepairFormationUnitRepairCapability(new RepairFormationUnitRepairCapabilityPK(
                                    repairFormationUnitId,
                                    equipmentId,
                                    repairTypeId,
                                    sessionId), capability);
                        }).collect(Collectors.toList());

        calculatedRepairCapabilitiesPerDayRepository.saveAll(repairFormationUnitRepairCapabilities);
    }

    @Override
    public Map<RepairFormationUnit, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            UUID sessionId,
            Long repairTypeId,
            List<Long> repairFormationUnitIds,
            List<Long> equipmentIds,
            List<Long> equipmentSubTypeIds,
            List<Long> equipmentTypeIds) {
        return internalGetCalculatedRepairCapabilities(
                sessionId, repairTypeId, repairFormationUnitIds, equipmentIds, equipmentSubTypeIds, equipmentTypeIds);
    }


    private Map<RepairFormationUnit, Map<Equipment, Double>> internalGetCalculatedRepairCapabilities(UUID sessionId,
                                                                                                     Long repairTypeId,
                                                                                                     List<Long> repairFormationUnitIds,
                                                                                                     List<Long> equipmentIds,
                                                                                                     List<Long> equipmentSubTypeIds,
                                                                                                     List<Long> equipmentTypeIds) {
        Iterable<RepairFormationUnitRepairCapability> calculatedRepairCapabilitesPerDays =
                calculatedRepairCapabilitiesPerDayRepository.findByIds(
                        sessionId,
                        repairTypeId,
                        repairFormationUnitIds,
                        equipmentIds,
                        equipmentSubTypeIds,
                        equipmentTypeIds);
        Map<RepairFormationUnit, Map<Equipment, Double>> result = new HashMap<>();
        for (RepairFormationUnitRepairCapability calculatedRepairCapabilitesPerDay : calculatedRepairCapabilitesPerDays) {
            RepairFormationUnit repairFormationUnit = calculatedRepairCapabilitesPerDay.getRepairFormationUnit();
            result.computeIfAbsent(repairFormationUnit, rs -> new HashMap<>());
            result.get(repairFormationUnit).put(calculatedRepairCapabilitesPerDay.getEquipment(),
                                                calculatedRepairCapabilitesPerDay.getCapability());
        }
        return result;
    }
}
