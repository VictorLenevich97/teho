package va.rit.teho.service.implementation.repairformation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.equipment.Equipment;
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
                                                                                                          RepairFormationUnitEquipmentStaff rsec) {
        return equipment -> {

            double calculatedCapability =
                    calculationService.calculateRepairCapabilities(
                            rsec.getTotalStaff(),
                            rsec
                                    .getRepairFormationUnit()
                                    .getRepairFormation()
                                    .getRepairFormationType()
                                    .getWorkingHoursMax(),
                            rsec.getRepairFormationUnit().getWorkhoursDistributionInterval().getUpperBound());
            Long repairFormationUnitId = rsec.getEquipmentPerRepairFormationUnit().getRepairFormationUnitId();
            Long equipmentId = equipment.getId();

            return calculatedRepairCapabilitiesPerDayRepository
                    .find(sessionId, repairFormationUnitId, equipmentId)
                    .map(crcpd -> crcpd.setCapability(calculatedCapability))
                    .orElse(new RepairFormationUnitRepairCapability(repairFormationUnitId,
                            equipmentId,
                            sessionId,
                            calculatedCapability));
        };
    }

    @Override
    public void copyRepairCapabilities(UUID originalSessionId, UUID newSessionId) {
        List<RepairFormationUnitRepairCapability> repairCapabilities =
                calculatedRepairCapabilitiesPerDayRepository.findFiltered(originalSessionId,
                        null,
                        null,
                        null);

        List<RepairFormationUnitRepairCapability> updatedRepairCapabilitesPerDayList =
                repairCapabilities.stream().map(crcpd -> crcpd.copy(newSessionId)).collect(Collectors.toList());

        calculatedRepairCapabilitiesPerDayRepository.saveAll(updatedRepairCapabilitesPerDayList);
    }

    @Override
    public void calculateAndUpdateRepairCapabilities(UUID sessionId) {
        List<RepairFormationUnitEquipmentStaff> repairFormationStaff =
                repairFormationUnitService.listEquipmentStaff(sessionId);
        calculateAndUpdateRepairCapabilities(sessionId, repairFormationStaff);
    }

    private void calculateAndUpdateRepairCapabilities(UUID sessionId,
                                                      List<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffList) {
        List<RepairFormationUnitRepairCapability> capabilitesPerDayList =
                repairFormationUnitEquipmentStaffList
                        .stream()
                        .flatMap(repairFormationEquipmentStaff ->
                                repairFormationEquipmentStaff
                                        .getEquipmentType()
                                        .getEquipmentSet()
                                        .stream()
                                        .map(getCalculatedRepairCapabilitesPerDay(sessionId, repairFormationEquipmentStaff)))
                        .collect(Collectors.toList());
        calculatedRepairCapabilitiesPerDayRepository.saveAll(capabilitesPerDayList);
    }

    @Override
    public void calculateAndUpdateRepairCapabilitiesPerRFU(UUID sessionId,
                                                           Long repairFormationUnitId) {
        List<RepairFormationUnitEquipmentStaff> repairFormationUnitStaff =
                repairFormationUnitService.listEquipmentStaff(sessionId, repairFormationUnitId);

        calculateAndUpdateRepairCapabilities(sessionId, repairFormationUnitStaff);
    }

    @Override
    public void updateRepairCapabilities(UUID sessionId,
                                         Long repairFormationUnitId,
                                         Map<Long, Double> capabilitiesMap) {
        List<RepairFormationUnitRepairCapability> repairFormationUnitRepairCapabilities =
                capabilitiesMap
                        .entrySet()
                        .stream()
                        .map(equipmentIdCapabilityEntry -> {
                            Long equipmentId = equipmentIdCapabilityEntry.getKey();
                            Double capability = equipmentIdCapabilityEntry.getValue();

                            return calculatedRepairCapabilitiesPerDayRepository
                                    .find(sessionId, repairFormationUnitId, equipmentId)
                                    .map(crcpd -> crcpd.setCapability(capability))
                                    .orElse(new RepairFormationUnitRepairCapability(
                                            repairFormationUnitId,
                                            equipmentId,
                                            sessionId,
                                            capability));
                        })
                        .collect(Collectors.toList());

        calculatedRepairCapabilitiesPerDayRepository.saveAll(repairFormationUnitRepairCapabilities);
    }

    @Override
    public RepairFormationUnitRepairCapability updateRepairCapabilities(
            UUID sessionId, Long repairFormationUnitId, Long equipmentId, Double capability) {
        RepairFormationUnitRepairCapability updated =
                calculatedRepairCapabilitiesPerDayRepository
                        .find(sessionId, repairFormationUnitId, equipmentId)
                        .map(crcpd -> crcpd.setCapability(capability))
                        .orElse(new RepairFormationUnitRepairCapability(
                                repairFormationUnitId,
                                equipmentId,
                                sessionId,
                                capability));

        return calculatedRepairCapabilitiesPerDayRepository.save(updated);
    }

    @Override
    public Map<Equipment, Double> getCalculatedRepairCapabilities(UUID sessionId,
                                                                  Long repairFormationUnitId,
                                                                  List<Long> equipmentIds,
                                                                  List<Long> equipmentTypeIds) {
        RepairFormationUnit repairFormationUnit = repairFormationUnitService.get(repairFormationUnitId);
        Map<RepairFormationUnit, Map<Equipment, Double>> result = internalGetCalculatedRepairCapabilities(
                sessionId,
                Collections.singletonList(repairFormationUnitId), equipmentIds, equipmentTypeIds);
        return result.getOrDefault(repairFormationUnit, Collections.emptyMap());
    }

    @Override
    public Map<RepairFormationUnit, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            UUID sessionId,
            List<Long> repairFormationUnitIds,
            List<Long> equipmentIds,
            List<Long> equipmentTypeIds) {
        return internalGetCalculatedRepairCapabilities(
                sessionId, repairFormationUnitIds, equipmentIds, equipmentTypeIds);
    }


    private Map<RepairFormationUnit, Map<Equipment, Double>> internalGetCalculatedRepairCapabilities(UUID sessionId,
                                                                                                     List<Long> repairFormationUnitIds,
                                                                                                     List<Long> equipmentIds,
                                                                                                     List<Long> equipmentTypeIds) {
        List<RepairFormationUnitRepairCapability> calculatedRepairCapabilitesPerDays =
                calculatedRepairCapabilitiesPerDayRepository.findFiltered(
                        sessionId,
                        repairFormationUnitIds,
                        equipmentIds,
                        equipmentTypeIds);

        return calculatedRepairCapabilitesPerDays
                .stream()
                .collect(Collectors.groupingBy(RepairFormationUnitRepairCapability::getRepairFormationUnit,
                        Collectors.toMap(RepairFormationUnitRepairCapability::getEquipment,
                                RepairFormationUnitRepairCapability::getCapability)));
    }
}
