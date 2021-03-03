package va.rit.teho.service.implementation.labordistribution;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.equipment.combined.EquipmentFailurePerRepairTypeAmount;
import va.rit.teho.entity.equipment.combined.EquipmentPerFormationFailureIntensityAndLaborInput;
import va.rit.teho.entity.labordistribution.LaborDistribution;
import va.rit.teho.entity.labordistribution.LaborDistributionPK;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;
import va.rit.teho.entity.labordistribution.combined.*;
import va.rit.teho.repository.equipment.EquipmentPerFormationFailureIntensityRepository;
import va.rit.teho.repository.labordistribution.LaborDistributionRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;
import va.rit.teho.service.labordistribution.WorkhoursDistributionIntervalService;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class LaborInputDistributionServiceImpl implements LaborInputDistributionService {

    private final CalculationService calculationService;
    private final EquipmentPerFormationService equipmentPerFormationService;
    private final RepairTypeService repairTypeService;
    private final LaborDistributionRepository laborDistributionRepository;
    private final EquipmentPerFormationFailureIntensityRepository equipmentPerFormationFailureIntensityRepository;
    private final WorkhoursDistributionIntervalService workhoursDistributionIntervalService;

    public LaborInputDistributionServiceImpl(
            WorkhoursDistributionIntervalService workhoursDistributionIntervalService,
            CalculationService calculationService,
            EquipmentPerFormationService equipmentPerFormationService,
            RepairTypeService repairTypeService,
            LaborDistributionRepository laborDistributionRepository,
            EquipmentPerFormationFailureIntensityRepository equipmentPerFormationFailureIntensityRepository) {
        this.workhoursDistributionIntervalService = workhoursDistributionIntervalService;
        this.calculationService = calculationService;
        this.equipmentPerFormationService = equipmentPerFormationService;
        this.repairTypeService = repairTypeService;
        this.laborDistributionRepository = laborDistributionRepository;
        this.equipmentPerFormationFailureIntensityRepository = equipmentPerFormationFailureIntensityRepository;
    }

    private EquipmentLaborInputDistribution buildLaborInputDistribution(List<LaborDistributionData> laborDistributionDataList) {
        LaborDistributionData eir = laborDistributionDataList.get(0);
        Map<Long, CountAndLaborInput> laborInputMap = new HashMap<>();
        for (LaborDistributionData eird : laborDistributionDataList) {
            if (laborInputMap.put(eird.getIntervalId(),
                    new CountAndLaborInput(eird.getCount(), eird.getAvgLaborInput())) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        return EquipmentLaborInputDistribution
                .builder()
                .formationName(eir.getEquipmentPerFormation().getFormation().getFullName())
                .equipmentName(eir.getEquipmentPerFormation().getEquipment().getName())
                .avgDailyFailure(eir.getAvgDailyFailure())
                .standardLaborInput(eir.getLaborInput())
                .countAndLaborInputCombinedData(Collections.singletonMap(eir.getRepairType(),
                        new CountAndLaborInputCombinedData(
                                laborInputMap)))
                .totalRepairComplexity(
                        laborInputMap.values().stream().mapToDouble(CountAndLaborInput::getLaborInput).sum())
                .build();
    }

    @Override
    public Map<EquipmentType, List<EquipmentLaborInputDistribution>> getLaborInputDistribution(
            UUID sessionId,
            Long repairTypeId,
            Long stageId,
            List<Long> equipmentTypeIds) {
        Map<EquipmentType, Map<EquipmentPerFormation, List<LaborDistributionData>>> grouped =
                laborDistributionRepository.findAllGrouped(sessionId, repairTypeId, stageId, equipmentTypeIds);
        Map<EquipmentType, List<EquipmentLaborInputDistribution>> result = new HashMap<>();

        grouped.forEach((equipmentType, subTypeMap) -> subTypeMap
                .forEach((key, equipmentInRepairDataList) ->
                        result.computeIfAbsent(equipmentType, k -> new ArrayList<>())
                                .add(buildLaborInputDistribution(equipmentInRepairDataList))));
        return result;
    }

    @Override
    public Map<EquipmentType, List<EquipmentLaborInputDistribution>> getAggregatedLaborInputDistribution(
            UUID sessionId, List<Long> formationIds, List<Long> equipmentIds) {
        List<LaborDistribution> aggregated = laborDistributionRepository.findByTehoSessionIdAndFilters(sessionId,
                formationIds,
                equipmentIds);
        List<EquipmentFailurePerRepairTypeAmount> equipmentFailurePerRepairTypeAmounts =
                equipmentPerFormationFailureIntensityRepository.listFailureDataWithLaborInputPerRepairType(sessionId,
                        formationIds,
                        equipmentIds);

        Map<EquipmentType, List<EquipmentLaborInputDistribution>> result = new HashMap<>();

        Map<EquipmentPerFormation, Pair<Map<RepairType, CountAndLaborInputCombinedData>, Double>> intermediate = new HashMap<>();

        for (EquipmentFailurePerRepairTypeAmount equipmentFailurePerRepairTypeAmount : equipmentFailurePerRepairTypeAmounts) {
            Pair<Map<RepairType, CountAndLaborInputCombinedData>, Double> laborInputMapAndDailyFailure =
                    calculateLaborInputMapAndSumDailyFailure(aggregated,
                            intermediate,
                            equipmentFailurePerRepairTypeAmount);
            intermediate.put(equipmentFailurePerRepairTypeAmount.getEquipmentPerFormation(),
                    laborInputMapAndDailyFailure);
        }

        intermediate.forEach((equipmentPerFormation, repairTypeCountAndLaborInputCombinedDataMap) -> {
            EquipmentLaborInputDistribution equipmentLaborInputDistribution =
                    EquipmentLaborInputDistribution
                            .builder()
                            .formationName(equipmentPerFormation.getFormation().getFullName())
                            .equipmentName(equipmentPerFormation.getEquipment().getName())
                            .equipmentAmount(equipmentPerFormation.getAmount())
                            .avgDailyFailure(repairTypeCountAndLaborInputCombinedDataMap.getSecond())
                            .countAndLaborInputCombinedData(repairTypeCountAndLaborInputCombinedDataMap.getFirst())
                            .totalRepairComplexity(0)
                            .build();

            result.computeIfAbsent(equipmentPerFormation.getEquipment().getEquipmentType(),
                    k -> new ArrayList<>()).add(equipmentLaborInputDistribution);
        });

        return result;
    }

    private Pair<Map<RepairType, CountAndLaborInputCombinedData>, Double> calculateLaborInputMapAndSumDailyFailure(List<LaborDistribution> aggregated,
                                                                                                                   Map<EquipmentPerFormation, Pair<Map<RepairType, CountAndLaborInputCombinedData>, Double>> intermediate,
                                                                                                                   EquipmentFailurePerRepairTypeAmount equipmentFailurePerRepairTypeAmount) {
        CountAndLaborInputCombinedData countAndLaborInputCombinedData;
        if (equipmentFailurePerRepairTypeAmount.getRepairType().includesIntervals()) {
            Map<Long, CountAndLaborInput> laborInputMap = new HashMap<>();
            aggregated
                    .stream()
                    .filter(ld ->
                            ld
                                    .getFormation()
                                    .equals(equipmentFailurePerRepairTypeAmount.getFormation()) &&
                                    ld
                                            .getEquipment()
                                            .equals(equipmentFailurePerRepairTypeAmount.getEquipment()) &&
                                    ld
                                            .getRepairType()
                                            .equals(equipmentFailurePerRepairTypeAmount.getRepairType()))
                    .forEach(ld -> laborInputMap.computeIfAbsent(ld.getWorkhoursDistributionInterval().getId(),
                            id -> CountAndLaborInput.createEmpty())
                            .add(ld.getCount(), ld.getAvgLaborInput()));
            countAndLaborInputCombinedData = new CountAndLaborInputCombinedData(laborInputMap);
        } else {
            countAndLaborInputCombinedData = new CountAndLaborInputCombinedData(equipmentFailurePerRepairTypeAmount.getAmount());
        }

        Pair<Map<RepairType, CountAndLaborInputCombinedData>, Double> existingMapAndFailureData =
                intermediate.getOrDefault(equipmentFailurePerRepairTypeAmount.getEquipmentPerFormation(),
                        Pair.of(new HashMap<>(), 0.0));
        existingMapAndFailureData
                .getFirst()
                .put(equipmentFailurePerRepairTypeAmount.getRepairType(), countAndLaborInputCombinedData);

        Double updatedData = existingMapAndFailureData.getSecond() + equipmentFailurePerRepairTypeAmount.getAmount();

        return Pair.of(existingMapAndFailureData.getFirst(), updatedData);
    }

    private LaborDistribution calculateLaborDistribution(UUID sessionId,
                                                         Long formationId,
                                                         Long equipmentId,
                                                         Double avgDailyFailure,
                                                         int standardLaborInput,
                                                         WorkhoursDistributionInterval interval,
                                                         Long stageId,
                                                         Long repairTypeId) {
        double count;
        double laborInput;

        if (interval.getLowerBound() != null && interval.getLowerBound() >= standardLaborInput) {
            count = 0;
            laborInput = 0;
        } else {
            count = calculationService.calculateEquipmentInRepairCount(interval.getUpperBound(),
                    interval.getLowerBound(),
                    avgDailyFailure,
                    standardLaborInput);
            laborInput = calculationService.calculateEquipmentInRepairLaborInput(count, interval.getUpperBound());
        }

        return new LaborDistribution(new LaborDistributionPK(formationId,
                equipmentId,
                interval.getId(),
                stageId,
                repairTypeId,
                sessionId),
                count,
                laborInput);
    }

    /*
     * 3 возможные ситуации:
     * 1. Если среднесуточный выход в ремонт <= нижней границы интервала, то пустой результат (по 0)
     * 2. Если среднесуточный выход в ремонт находится "между" верхней и нижней границей, то это "последний" интервал в данном расчете, поэтому
     * заносим в count оставшееся количество выходящих из строя ВВСТ
     * 3. Во всех остальных случаях используем формулу
     * */
    private Stream<LaborDistribution> calculateEquipmentLaborInputDistribution(
            UUID sessionId,
            Long stageId,
            Long repairTypeId,
            Double avgDailyFailure,
            EquipmentPerFormationFailureIntensityAndLaborInput equipmentPerFormationAndLaborInput) {
        AtomicReference<Double> sum = new AtomicReference<>(0.0);
        int laborInput = equipmentPerFormationAndLaborInput.getLaborInput();
        Long formationId = equipmentPerFormationAndLaborInput.getFormationId();
        Long equipmentId = equipmentPerFormationAndLaborInput.getEquipmentId();
        return workhoursDistributionIntervalService
                .list()
                .stream()
                .sorted(Comparator.comparing(WorkhoursDistributionInterval::getLowerBound,
                        Comparator.nullsFirst(Comparator.naturalOrder())))
                .map(interval -> {
                    LaborDistribution result;
                    if ((interval.getLowerBound() == null || laborInput > interval.getLowerBound()) &&
                            (interval.getUpperBound() == null || laborInput < interval.getUpperBound())) {
                        result = new LaborDistribution(
                                new LaborDistributionPK(formationId, equipmentId, interval.getId(), stageId, repairTypeId, sessionId),
                                avgDailyFailure - sum.get(),
                                0);
                    } else {
                        if (interval.getLowerBound() != null &&
                                interval.getLowerBound() >= laborInput) {
                            result = LaborDistribution.empty(
                                    formationId, equipmentId, interval.getId(), stageId, repairTypeId, sessionId);
                        } else {
                            result = calculateLaborDistribution(
                                    sessionId, formationId, equipmentId, avgDailyFailure, laborInput, interval, stageId, repairTypeId);
                        }
                    }
                    sum.updateAndGet(v -> v + result.getCount());
                    return result;
                });
    }

    private void cleanupSessionData(UUID sessionId) {
        laborDistributionRepository.deleteAll(laborDistributionRepository.findByTehoSessionId(sessionId));
    }

    private void calculateAndSave(Long repairTypeId,
                                  UUID sessionId,
                                  List<EquipmentPerFormationFailureIntensityAndLaborInput> equipmentPerFormations) {
        List<LaborDistribution> calculated =
                equipmentPerFormations
                        .stream()
                        .flatMap(epb -> this.calculateEquipmentLaborInputDistribution(sessionId,
                                epb.getStageId(),
                                repairTypeId,
                                epb.getAvgDailyFailure(),
                                epb))
                        .collect(Collectors.toList());
        laborDistributionRepository.saveAll(calculated);
    }

    @Override
    @Transactional
    public void updateLaborInputDistribution(UUID sessionId, List<Long> equipmentIds, List<Long> formationIds) {
        cleanupSessionData(sessionId);
        List<RepairType> repairTypeList = repairTypeService
                .list(true)
                .stream()
                .filter(RepairType::includesIntervals)
                .collect(Collectors.toList());
        repairTypeList
                .forEach(repairType -> {
                    List<EquipmentPerFormationFailureIntensityAndLaborInput> equipmentPerFormations =
                            equipmentPerFormationService.listWithIntensityAndLaborInput(sessionId,
                                    repairType.getId(),
                                    equipmentIds,
                                    formationIds);
                    calculateAndSave(repairType.getId(), sessionId, equipmentPerFormations);
                });
    }

    @Override
    public void copyLaborInputDistributionData(UUID originalSessionId, UUID newSessionId) {
        List<LaborDistribution> laborDistributionList =
                laborDistributionRepository.findByTehoSessionId(originalSessionId);

        List<LaborDistribution> updatedLaborDistributionList =
                laborDistributionList.stream().map(eir -> eir.copy(newSessionId)).collect(Collectors.toList());

        laborDistributionRepository.saveAll(updatedLaborDistributionList);
    }

    @Override
    public List<LaborDistributionAggregatedData> listAggregatedDataForSessionAndFormation(UUID sessionId,
                                                                                          Long formationId,
                                                                                          List<Long> equipmentIds) {
        return laborDistributionRepository.selectLaborDistributionAggregatedData(sessionId, formationId, equipmentIds);
    }

}
