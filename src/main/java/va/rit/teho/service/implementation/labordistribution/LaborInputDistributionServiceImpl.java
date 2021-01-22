package va.rit.teho.service.implementation.labordistribution;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensityAndLaborInput;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.*;
import va.rit.teho.repository.labordistribution.LaborDistributionRepository;
import va.rit.teho.repository.labordistribution.WorkhoursDistributionIntervalRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class LaborInputDistributionServiceImpl implements LaborInputDistributionService {

    private final CalculationService calculationService;
    private final EquipmentPerFormationService equipmentPerFormationService;
    private final RepairTypeService repairTypeService;
    private final LaborDistributionRepository laborDistributionRepository;
    private final WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository;

    public LaborInputDistributionServiceImpl(
            WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository,
            CalculationService calculationService,
            EquipmentPerFormationService equipmentPerFormationService,
            RepairTypeService repairTypeService,
            LaborDistributionRepository laborDistributionRepository) {
        this.workhoursDistributionIntervalRepository = workhoursDistributionIntervalRepository;
        this.calculationService = calculationService;
        this.equipmentPerFormationService = equipmentPerFormationService;
        this.repairTypeService = repairTypeService;
        this.laborDistributionRepository = laborDistributionRepository;
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
                .formationName(eir.getFormationName())
                .equipmentName(eir.getEquipmentName())
                .avgDailyFailure(eir.getAvgDailyFailure())
                .standardLaborInput(eir.getLaborInput())
                .intervalCountAndLaborInputMap(laborInputMap)
                .totalRepairComplexity(
                        laborInputMap.values().stream().mapToDouble(CountAndLaborInput::getLaborInput).sum())
                .build();
    }

    @Override
    public Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> getLaborInputDistribution(
            UUID sessionId,
            Long repairTypeId,
            Long stageId,
            List<Long> equipmentTypeIds) {
        Map<EquipmentType, Map<EquipmentSubType, Map<LaborDistributionData.CompositeKey, List<LaborDistributionData>>>> grouped =
                laborDistributionRepository.findAllGrouped(sessionId, repairTypeId, stageId, equipmentTypeIds);
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> result = new HashMap<>();

        grouped.forEach((equipmentType, subTypeMap) -> subTypeMap
                .forEach((subType, compositeKeyMap) -> compositeKeyMap
                        .forEach((key, equipmentInRepairDataList) ->
                                         result.computeIfAbsent(equipmentType, k -> new HashMap<>())
                                               .computeIfAbsent(subType, k -> new ArrayList<>())
                                               .add(buildLaborInputDistribution(equipmentInRepairDataList)))));
        return result;
    }

    private LaborDistribution calculateLaborDistribution(UUID sessionId,
                                                         Long formationId,
                                                         Long equipmentId,
                                                         Double avgDailyFailure,
                                                         int standardLaborInput,
                                                         WorkhoursDistributionInterval interval,
                                                         Long stageId,
                                                         Long repairTypeId) {
        double count = calculationService.calculateEquipmentInRepairCount(interval.getUpperBound(),
                                                                          interval.getLowerBound(),
                                                                          avgDailyFailure,
                                                                          standardLaborInput);
        double laborInput = calculationService.calculateEquipmentInRepairLaborInput(count, interval.getUpperBound());

        return new LaborDistribution(new LaborDistributionPK(formationId,
                                                             equipmentId,
                                                             interval.getId(),
                                                             stageId,
                                                             repairTypeId,
                                                             sessionId),
                                     count,
                                     laborInput);
    }

    private Stream<LaborDistribution> calculateEquipmentLaborInputDistribution(
            UUID sessionId,
            Long stageId,
            Long repairTypeId,
            Double avgDailyFailure,
            EquipmentPerFormationFailureIntensityAndLaborInput equipmentPerFormationAndLaborInput) {
        return StreamSupport
                .stream(workhoursDistributionIntervalRepository.findAll().spliterator(), false)
                .map(interval -> calculateLaborDistribution(sessionId,
                                                            equipmentPerFormationAndLaborInput.getFormationId(),
                                                            equipmentPerFormationAndLaborInput.getEquipmentId(),
                                                            avgDailyFailure,
                                                            equipmentPerFormationAndLaborInput.getLaborInput(),
                                                            interval,
                                                            stageId,
                                                            repairTypeId));
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
    public void updateLaborInputDistribution(UUID sessionId) {
        List<RepairType> repairTypeList = repairTypeService.list(true);
        repairTypeList
                .forEach(repairType -> {
                    List<EquipmentPerFormationFailureIntensityAndLaborInput> equipmentPerFormations =
                            equipmentPerFormationService.listWithIntensityAndLaborInput(sessionId, repairType.getId());
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
    public List<WorkhoursDistributionInterval> listDistributionIntervals() {
        return (List<WorkhoursDistributionInterval>) workhoursDistributionIntervalRepository.findAll();
    }

    public List<LaborDistributionAggregatedData> listAggregatedDataForSessionAndFormation(Long formationId,
                                                                                          UUID sessionId) {
        return laborDistributionRepository.selectLaborDistributionAggregatedData(formationId, sessionId);
    }

}
