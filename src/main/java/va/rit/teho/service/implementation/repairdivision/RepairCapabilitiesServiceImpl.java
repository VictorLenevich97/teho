package va.rit.teho.service.implementation.repairdivision;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentLaborInputPerType;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitRepairCapability;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitRepairCapabilityPK;
import va.rit.teho.entity.repairdivision.RepairDivisionUnit;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitEquipmentStaff;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairdivision.RepairDivisionUnitRepairCapabilityRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.repairdivision.RepairCapabilitiesService;
import va.rit.teho.service.repairdivision.RepairDivisionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class RepairCapabilitiesServiceImpl implements RepairCapabilitiesService {

    private final RepairDivisionUnitRepairCapabilityRepository calculatedRepairCapabilitiesPerDayRepository;

    private final CalculationService calculationService;
    private final RepairDivisionService repairDivisionService;

    public RepairCapabilitiesServiceImpl(
            RepairDivisionUnitRepairCapabilityRepository calculatedRepairCapabilitiesPerDayRepository,
            CalculationService calculationService,
            RepairDivisionService repairDivisionService) {
        this.calculatedRepairCapabilitiesPerDayRepository = calculatedRepairCapabilitiesPerDayRepository;
        this.calculationService = calculationService;
        this.repairDivisionService = repairDivisionService;
    }

    private Function<Equipment, RepairDivisionUnitRepairCapability> getCalculatedRepairCapabilitesPerDay(UUID sessionId,
                                                                                                         Long repairTypeId,
                                                                                                         RepairDivisionUnitEquipmentStaff rsec) {
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
                            rsec.getTotalStaff() * rsec.getRepairDivisionUnit().getStationAmount(),
                            rsec.getRepairDivisionUnit().getRepairDivisionUnitType().getWorkingHoursMax(),
                            laborInputPerType.getAmount());

            RepairDivisionUnitRepairCapabilityPK stationWithRepairType =
                    new RepairDivisionUnitRepairCapabilityPK(
                            rsec.getEquipmentPerRepairDivisionUnit().getRepairDivisionUnitId(),
                            equipment.getId(),
                            repairTypeId,
                            sessionId);
            return new RepairDivisionUnitRepairCapability(stationWithRepairType, calculatedCapabilities);
        };
    }

    @Override
    public void copyRepairCapabilities(UUID originalSessionId, UUID newSessionId) {
        List<RepairDivisionUnitRepairCapability> repairCapabilities =
                calculatedRepairCapabilitiesPerDayRepository.findByIds(originalSessionId, null, null, null, null, null);

        List<RepairDivisionUnitRepairCapability> updatedRepairCapabilitesPerDayList =
                repairCapabilities.stream().map(crcpd -> crcpd.copy(newSessionId)).collect(Collectors.toList());

        calculatedRepairCapabilitiesPerDayRepository.saveAll(updatedRepairCapabilitesPerDayList);
    }

    @Override
    public void calculateAndUpdateRepairCapabilities(UUID sessionId, Long repairTypeId) {
        List<RepairDivisionUnitEquipmentStaff> repairDivisionStaff = repairDivisionService.listRepairDivisionUnitEquipmentStaff(sessionId);
        calculateAndUpdateRepairCapabilities(sessionId, repairDivisionStaff, repairTypeId);
    }

    private void calculateAndUpdateRepairCapabilities(UUID sessionId,
                                                      List<RepairDivisionUnitEquipmentStaff> repairDivisionUnitEquipmentStaffList,
                                                      Long repairTypeId) {
        List<RepairDivisionUnitRepairCapability> capabilitesPerDayList =
                repairDivisionUnitEquipmentStaffList.stream().flatMap(
                        repairDivisionEquipmentStaff ->
                                repairDivisionEquipmentStaff
                                        .getEquipmentSubType()
                                        .getEquipmentSet()
                                        .stream()
                                        .map(getCalculatedRepairCapabilitesPerDay(sessionId,
                                                                                  repairTypeId,
                                                                                  repairDivisionEquipmentStaff)))
                                                    .collect(Collectors.toList());
        calculatedRepairCapabilitiesPerDayRepository.saveAll(capabilitesPerDayList);
    }

    @Override
    public void calculateAndUpdateRepairCapabilitiesPerStation(UUID sessionId,
                                                               Long repairDivisionUnitId,
                                                               Long repairTypeId) {
        List<RepairDivisionUnitEquipmentStaff> repairDivisionUnitStaff =
                repairDivisionService.listRepairDivisionUnitEquipmentStaff(repairDivisionUnitId, sessionId);

        calculateAndUpdateRepairCapabilities(sessionId, repairDivisionUnitStaff, repairTypeId);
    }

    @Override
    public Map<RepairDivisionUnit, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            UUID sessionId,
            Long repairTypeId,
            List<Long> repairDivisionUnitIds,
            List<Long> equipmentIds,
            List<Long> equipmentSubTypeIds,
            List<Long> equipmentTypeIds) {
        return internalGetCalculatedRepairCapabilities(
                sessionId, repairTypeId, repairDivisionUnitIds, equipmentIds, equipmentSubTypeIds, equipmentTypeIds);
    }



    private Map<RepairDivisionUnit, Map<Equipment, Double>> internalGetCalculatedRepairCapabilities(UUID sessionId,
                                                                                                    Long repairTypeId,
                                                                                                    List<Long> repairDivisionUnitIds,
                                                                                                    List<Long> equipmentIds,
                                                                                                    List<Long> equipmentSubTypeIds,
                                                                                                    List<Long> equipmentTypeIds) {
        Iterable<RepairDivisionUnitRepairCapability> calculatedRepairCapabilitesPerDays =
                calculatedRepairCapabilitiesPerDayRepository.findByIds(
                        sessionId,
                        repairTypeId,
                        repairDivisionUnitIds,
                        equipmentIds,
                        equipmentSubTypeIds,
                        equipmentTypeIds);
        Map<RepairDivisionUnit, Map<Equipment, Double>> result = new HashMap<>();
        for (RepairDivisionUnitRepairCapability calculatedRepairCapabilitesPerDay : calculatedRepairCapabilitesPerDays) {
            RepairDivisionUnit repairDivisionUnit = calculatedRepairCapabilitesPerDay.getRepairDivisionUnit();
            result.computeIfAbsent(repairDivisionUnit, rs -> new HashMap<>());
            result.get(repairDivisionUnit).put(calculatedRepairCapabilitesPerDay.getEquipment(),
                                               calculatedRepairCapabilitesPerDay.getCapability());
        }
        return result;
    }
}
