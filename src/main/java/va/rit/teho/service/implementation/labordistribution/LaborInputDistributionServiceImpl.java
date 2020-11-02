package va.rit.teho.service.implementation.labordistribution;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.EquipmentPerBaseFailureIntensityAndLaborInput;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.*;
import va.rit.teho.repository.labordistribution.LaborDistributionRepository;
import va.rit.teho.repository.labordistribution.WorkhoursDistributionIntervalRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.equipment.EquipmentPerBaseService;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class LaborInputDistributionServiceImpl implements LaborInputDistributionService {

    private final CalculationService calculationService;
    private final EquipmentPerBaseService equipmentPerBaseService;
    private final RepairTypeService repairTypeService;

    private final LaborDistributionRepository laborDistributionRepository;
    private final WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository;

    public LaborInputDistributionServiceImpl(
            WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository,
            CalculationService calculationService,
            EquipmentPerBaseService equipmentPerBaseService,
            RepairTypeService repairTypeService,
            LaborDistributionRepository laborDistributionRepository) {
        this.workhoursDistributionIntervalRepository = workhoursDistributionIntervalRepository;
        this.calculationService = calculationService;
        this.equipmentPerBaseService = equipmentPerBaseService;
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
                                                         Long baseId,
                                                         Long equipmentId,
                                                         double avgDailyFailure,
                                                         int standardLaborInput,
                                                         WorkhoursDistributionInterval interval,
                                                         Long stageId) {
        double count = calculationService.calculateEquipmentInRepairCount(interval.getUpperBound(),
                                                                          interval.getLowerBound(),
                                                                          avgDailyFailure,
                                                                          standardLaborInput);
        double laborInput = calculationService.calculateEquipmentInRepairLaborInput(count, interval.getUpperBound());

        return new LaborDistribution(new LaborDistributionPK(baseId, equipmentId, interval.getId(), stageId, sessionId),
                                     count,
                                     laborInput);
    }

    private Stream<LaborDistribution> calculateEquipmentLaborInputDistribution(
            UUID sessionId,
            Long stageId,
            double avgDailyFailure,
            EquipmentPerBaseFailureIntensityAndLaborInput equipmentPerBaseAndLaborInput) {
        return StreamSupport
                .stream(workhoursDistributionIntervalRepository.findAll().spliterator(), false)
                .map(interval -> calculateLaborDistribution(sessionId,
                                                            equipmentPerBaseAndLaborInput.getBaseId(),
                                                            equipmentPerBaseAndLaborInput.getEquipmentId(),
                                                            avgDailyFailure,
                                                            equipmentPerBaseAndLaborInput.getLaborInput(),
                                                            interval,
                                                            stageId));
    }

    private void calculateAndSave(UUID sessionId,
                                  List<EquipmentPerBaseFailureIntensityAndLaborInput> equipmentPerBases) {
        List<LaborDistribution> calculated =
                equipmentPerBases
                        .stream()
                        .flatMap(epb -> this.calculateEquipmentLaborInputDistribution(sessionId,
                                                                                      epb.getStageId(),
                                                                                      epb.getAvgDailyFailure(),
                                                                                      epb))
                        .collect(Collectors.toList());
        laborDistributionRepository.saveAll(calculated);
    }

    @Override
    public void updateLaborInputDistribution(UUID sessionId) {
        List<RepairType> repairTypeList = repairTypeService.list(true);
        repairTypeList
                .forEach(repairType -> calculateAndSave(sessionId,
                                                        equipmentPerBaseService.listWithIntensityAndLaborInput(
                                                                sessionId,
                                                                repairType.getId())));
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
    public List<WorkhoursDistributionInterval> getDistributionIntervals() {
        return (List<WorkhoursDistributionInterval>) workhoursDistributionIntervalRepository.findAll();
    }
}
