package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.*;
import va.rit.teho.enums.RepairTypeEnum;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.EquipmentInRepairRepository;
import va.rit.teho.repository.EquipmentPerBaseRepository;
import va.rit.teho.repository.RepairTypeRepository;
import va.rit.teho.repository.WorkhoursDistributionIntervalRepository;
import va.rit.teho.service.CalculationService;
import va.rit.teho.service.LaborInputDistributionService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class LaborInputDistributionServiceImpl implements LaborInputDistributionService {
    private static final RepairTypeEnum REPAIR_TYPE = RepairTypeEnum.CURRENT_REPAIR;

    private final CalculationService calculationService;

    private final EquipmentPerBaseRepository equipmentPerBaseRepository;
    private final EquipmentInRepairRepository equipmentInRepairRepository;
    private final RepairTypeRepository repairTypeRepository;
    private final WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository;

    public LaborInputDistributionServiceImpl(
            EquipmentPerBaseRepository equipmentPerBaseRepository,
            WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository,
            CalculationService calculationService,
            EquipmentInRepairRepository equipmentInRepairRepository,
            RepairTypeRepository repairTypeRepository) {
        this.equipmentPerBaseRepository = equipmentPerBaseRepository;
        this.workhoursDistributionIntervalRepository = workhoursDistributionIntervalRepository;
        this.calculationService = calculationService;
        this.equipmentInRepairRepository = equipmentInRepairRepository;
        this.repairTypeRepository = repairTypeRepository;
    }

    private EquipmentLaborInputDistribution buildLaborInputDistribution(List<EquipmentInRepairData> equipmentInRepairDataList) {
        EquipmentInRepairData eir = equipmentInRepairDataList.get(0);
        Map<Long, CountAndLaborInput> laborInputMap = equipmentInRepairDataList
                .stream()
                .collect(Collectors.toMap(EquipmentInRepairData::getIntervalId,
                                          eird -> new CountAndLaborInput(eird.getCount(), eird.getLaborInput())));

        return EquipmentLaborInputDistribution
                .builder()
                .baseName(eir.getBaseName())
                .equipmentName(eir.getEquipmentName())
                .avgDailyFailure(eir.getCount())
                .standardLaborInput(eir.getLaborInput())
                .intervalCountAndLaborInputMap(laborInputMap)
                .totalRepairComplexity(
                        laborInputMap.values().stream().mapToDouble(CountAndLaborInput::getLaborInput).sum())
                .build();
    }

    @Override
    public Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> getLaborInputDistribution(
            UUID sessionId,
            List<Long> equipmentTypeIds) {
        Long repairTypeId = getDefaultRepairTypeId();
        Map<EquipmentType, Map<EquipmentSubType, Map<EquipmentInRepairData.CompositeKey, List<EquipmentInRepairData>>>> grouped =
                equipmentInRepairRepository.findAllGrouped(sessionId, repairTypeId, equipmentTypeIds);
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> result = new HashMap<>();

        grouped.forEach((equipmentType, subTypeMap) -> subTypeMap
                .forEach((subType, compositeKeyMap) -> compositeKeyMap
                        .forEach((key, equipmentInRepairDataList) ->
                                         result.computeIfAbsent(equipmentType, k -> new HashMap<>())
                                               .computeIfAbsent(subType, k -> new ArrayList<>())
                                               .add(buildLaborInputDistribution(equipmentInRepairDataList)))));
        return result;
    }

    private Long getDefaultRepairTypeId() {
        return repairTypeRepository
                .findByName(LaborInputDistributionServiceImpl.REPAIR_TYPE.getName())
                .orElseThrow(() -> new NotFoundException("Тип ремонта не найден"))
                .getId();
    }

    private EquipmentInRepair calculateEquipmentInRepair(UUID sessionId,
                                                         Long baseId,
                                                         Long equipmentId,
                                                         double avgDailyFailure,
                                                         int standardLaborInput,
                                                         WorkhoursDistributionInterval interval) {
        double count = calculationService.calculateEquipmentRequiringRepair(interval.getUpperBound(),
                                                                            interval.getLowerBound(),
                                                                            avgDailyFailure,
                                                                            standardLaborInput);
        double laborInput = calculationService.calculateEquipmentRepairComplexity(count, interval.getUpperBound());

        return new EquipmentInRepair(new EquipmentInRepairId(baseId, equipmentId, interval.getId(), sessionId),
                                     count,
                                     laborInput);
    }

    private Stream<EquipmentInRepair> calculateEquipmentLaborInputDistribution(
            UUID sessionId,
            double coefficient,
            EquipmentPerBaseWithLaborInput equipmentPerBaseAndLaborInput) {
        EquipmentPerBase equipmentPerBase = equipmentPerBaseAndLaborInput.getEquipmentPerBase();
        double avgDailyFailure =
                calculationService.calculateEquipmentFailureAmount(
                        equipmentPerBase.getAmount(), equipmentPerBase.getIntensity(), coefficient);
        return StreamSupport
                .stream(workhoursDistributionIntervalRepository.findAll().spliterator(), false)
                .map(interval -> calculateEquipmentInRepair(sessionId,
                                                            equipmentPerBase.getBase().getId(),
                                                            equipmentPerBase.getEquipment().getId(),
                                                            avgDailyFailure,
                                                            equipmentPerBaseAndLaborInput.getLaborInput(),
                                                            interval));
    }

    private void calculateAndSave(UUID sessionId,
                                  double coefficient,
                                  List<EquipmentPerBaseWithLaborInput> equipmentPerBases) {
        List<EquipmentInRepair> calculated =
                equipmentPerBases
                        .stream()
                        .flatMap(epb -> this.calculateEquipmentLaborInputDistribution(sessionId, coefficient, epb))
                        .collect(Collectors.toList());
        equipmentInRepairRepository.saveAll(calculated);
    }

    @Override
    @Transactional
    public void updateLaborInputDistribution(UUID sessionId, double coefficient) {
        calculateAndSave(sessionId,
                         coefficient,
                         equipmentPerBaseRepository.findAllWithLaborInput(getDefaultRepairTypeId()));
    }

    @Override
    @Transactional
    public void updateLaborInputDistributionPerBase(UUID sessionId, double coefficient, Long baseId) {
        calculateAndSave(sessionId,
                         coefficient,
                         equipmentPerBaseRepository.findAllWithLaborInputAndBase(getDefaultRepairTypeId(), baseId));
    }

    @Override
    public void updateLaborInputDistributionPerEquipmentSubType(UUID sessionId,
                                                                double coefficient,
                                                                Long equipmentSubTypeId) {
        calculateAndSave(sessionId,
                         coefficient,
                         equipmentPerBaseRepository.findAllWithLaborInputAndEquipmentSubType(getDefaultRepairTypeId(),
                                                                                             equipmentSubTypeId));
    }

    @Override
    public void updateLaborInputDistributionPerEquipmentType(UUID sessionId, double coefficient, Long equipmentType) {
        calculateAndSave(sessionId,
                         coefficient,
                         equipmentPerBaseRepository.findAllWithLaborInputAndEquipmentType(getDefaultRepairTypeId(),
                                                                                          equipmentType));
    }

    @Override
    public void copyLaborInputDistributionData(UUID originalSessionId, UUID newSessionId) {
        List<EquipmentInRepair> equipmentInRepairList =
                equipmentInRepairRepository.findByTehoSessionId(originalSessionId);

        List<EquipmentInRepair> updatedEquipmentInRepairList =
                equipmentInRepairList.stream().map(eir -> eir.copy(newSessionId)).collect(Collectors.toList());

        equipmentInRepairRepository.saveAll(updatedEquipmentInRepairList);
    }

    @Override
    public List<WorkhoursDistributionInterval> getDistributionIntervals() {
        return (List<WorkhoursDistributionInterval>) workhoursDistributionIntervalRepository.findAll();
    }
}
