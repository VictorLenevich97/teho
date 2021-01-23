package va.rit.teho.service.implementation.repairformation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentLaborInputPerType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapability;
import va.rit.teho.repository.repairformation.RepairFormationUnitRepairCapabilityRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.repairformation.RepairCapabilitiesService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;

import java.util.Collections;
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
        return equipment -> {
            Integer laborInput = equipment
                    .getLaborInputPerTypes()
                    .stream()
                    .filter(lipt -> lipt.getRepairType().getId().equals(repairTypeId))
                    .findFirst()
                    .map(EquipmentLaborInputPerType::getAmount)
                    .orElse(0);

            double calculatedCapability =
                    calculationService.calculateRepairCapabilities(
                            rsec.getTotalStaff() * rsec.getRepairFormationUnit().getStationAmount(),
                            rsec
                                    .getRepairFormationUnit()
                                    .getRepairFormation()
                                    .getRepairFormationType()
                                    .getWorkingHoursMax(),
                            laborInput);

            return new RepairFormationUnitRepairCapability(rsec
                                                                   .getEquipmentPerRepairFormationUnit()
                                                                   .getRepairFormationUnitId(),
                                                           equipment.getId(),
                                                           repairTypeId,
                                                           sessionId,
                                                           calculatedCapability);
        };
    }

    @Override
    public void copyRepairCapabilities(UUID originalSessionId, UUID newSessionId) {
        List<RepairFormationUnitRepairCapability> repairCapabilities =
                calculatedRepairCapabilitiesPerDayRepository.findFiltered(originalSessionId,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null);

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
                repairFormationUnitEquipmentStaffList
                        .stream()
                        .flatMap(
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
                capabilitiesMap
                        .entrySet()
                        .stream()
                        .map(equipmentIdCapabilityEntry -> {
                            Long equipmentId = equipmentIdCapabilityEntry.getKey();
                            Double capability = equipmentIdCapabilityEntry.getValue();

                            return new RepairFormationUnitRepairCapability(
                                    repairFormationUnitId,
                                    equipmentId,
                                    repairTypeId,
                                    sessionId,
                                    capability);
                        })
                        .collect(Collectors.toList());

        calculatedRepairCapabilitiesPerDayRepository.saveAll(repairFormationUnitRepairCapabilities);
    }

    @Override
    public RepairFormationUnitRepairCapability updateRepairCapabilities(
            UUID sessionId, Long repairFormationUnitId, Long repairTypeId, Long equipmentId, Double capability) {
        return calculatedRepairCapabilitiesPerDayRepository.save(
                new RepairFormationUnitRepairCapability(repairFormationUnitId,
                                                        equipmentId,
                                                        repairTypeId,
                                                        sessionId,
                                                        capability));
    }

    @Override
    public Map<Equipment, Double> getCalculatedRepairCapabilities(Long repairFormationUnitId,
                                                                  UUID sessionId,
                                                                  Long repairTypeId,
                                                                  List<Long> equipmentIds,
                                                                  List<Long> equipmentSubTypeIds,
                                                                  List<Long> equipmentTypeIds) {
        RepairFormationUnit repairFormationUnit = repairFormationUnitService.get(repairFormationUnitId);
        Map<RepairFormationUnit, Map<Equipment, Double>> result = internalGetCalculatedRepairCapabilities(
                sessionId, repairTypeId,
                Collections.singletonList(repairFormationUnitId), equipmentIds, equipmentSubTypeIds, equipmentTypeIds);
        return result.getOrDefault(repairFormationUnit, Collections.emptyMap());
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
        List<RepairFormationUnitRepairCapability> calculatedRepairCapabilitesPerDays =
                calculatedRepairCapabilitiesPerDayRepository.findFiltered(
                        sessionId,
                        repairTypeId,
                        repairFormationUnitIds,
                        equipmentIds,
                        equipmentSubTypeIds,
                        equipmentTypeIds);

        return calculatedRepairCapabilitesPerDays
                .stream()
                .collect(Collectors.groupingBy(RepairFormationUnitRepairCapability::getRepairFormationUnit,
                                               Collectors.toMap(RepairFormationUnitRepairCapability::getEquipment,
                                                                RepairFormationUnitRepairCapability::getCapability)));
    }
}
