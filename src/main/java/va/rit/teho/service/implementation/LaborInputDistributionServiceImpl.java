package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.*;
import va.rit.teho.enums.RepairTypeEnum;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.model.Pair;
import va.rit.teho.repository.*;
import va.rit.teho.service.CalculationService;
import va.rit.teho.service.LaborInputDistributionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class LaborInputDistributionServiceImpl implements LaborInputDistributionService {
    private static final RepairTypeEnum REPAIR_TYPE = RepairTypeEnum.CURRENT_REPAIR;

    private final CalculationService calculationService;

    private final EquipmentTypeRepository equipmentTypeRepository;
    private final EquipmentPerBaseRepository equipmentPerBaseRepository;
    private final EquipmentInRepairRepository equipmentInRepairRepository;
    private final RepairTypeRepository repairTypeRepository;
    private final WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository;

    public LaborInputDistributionServiceImpl(
            EquipmentTypeRepository equipmentTypeRepository,
            EquipmentPerBaseRepository equipmentPerBaseRepository,
            WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository,
            CalculationService calculationService,
            EquipmentInRepairRepository equipmentInRepairRepository,
            RepairTypeRepository repairTypeRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.equipmentPerBaseRepository = equipmentPerBaseRepository;
        this.workhoursDistributionIntervalRepository = workhoursDistributionIntervalRepository;
        this.calculationService = calculationService;
        this.equipmentInRepairRepository = equipmentInRepairRepository;
        this.repairTypeRepository = repairTypeRepository;
    }

    private EquipmentLaborInputDistribution getEquipmentLaborInputDistribution(
            Pair<EquipmentInRepairData, HashMap<Long, Pair<Double, Double>>> pair) {
        EquipmentInRepairData eir = pair.getLeft();
        Map<Long, CountAndLaborInput> laborInputMap = new HashMap<>();
        for (Map.Entry<Long, Pair<Double, Double>> e : pair.getRight().entrySet()) {
            laborInputMap.put(e.getKey(), new CountAndLaborInput(e.getValue().getLeft(), e.getValue().getRight()));
        }
        return EquipmentLaborInputDistribution
                .builder()
                .baseName(eir.getBaseName())
                .equipmentType(eir.getCompositeKey().getSubType().getEquipmentType())
                .equipmentSubType(eir.getCompositeKey().getSubType())
                .equipmentName(eir.getEquipmentName())
                .avgDailyFailure(eir.getCount())
                .standardLaborInput(eir.getLaborInput())
                .intervalCountAndLaborInputMap(laborInputMap)
                .totalRepairComplexity(laborInputMap
                                               .values()
                                               .stream()
                                               .mapToDouble(CountAndLaborInput::getLaborInput)
                                               .sum())
                .build();
    }

    private void buildLaborDistributionMap(Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> result,
                                           EquipmentType equipmentType,
                                           EquipmentSubType subType,
                                           List<EquipmentInRepairData> equipmentInRepairDataList) {
        Pair<EquipmentInRepairData, HashMap<Long, Pair<Double, Double>>> repairDataHashMapPair =
                equipmentInRepairDataList
                        .stream()
                        .reduce(Pair.of(new EquipmentInRepairData(), new HashMap<>()),
                                (p, eir) -> {
                                    p.getRight()
                                     .put(eir.getIntervalId(), Pair.of(eir.getCount(), eir.getAvgLaborInput()));
                                    return Pair.of(eir, p.getRight());
                                },
                                (l, r) -> l);
        result.computeIfAbsent(equipmentType, k -> new HashMap<>())
              .computeIfAbsent(subType, k -> new ArrayList<>())
              .add(getEquipmentLaborInputDistribution(repairDataHashMapPair));
    }

    @Override
    public Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> getLaborInputDistribution(
            List<Long> equipmentTypeIds) {
        List<Long> equipmentTypeIdsFinal;
        if (equipmentTypeIds.isEmpty()) {
            equipmentTypeIdsFinal = StreamSupport
                    .stream(equipmentTypeRepository.findAll().spliterator(), false)
                    .map(EquipmentType::getId)
                    .collect(Collectors.toList());
        } else {
            equipmentTypeIdsFinal = equipmentTypeIds;
        }
        Long repairTypeId = getDefaultRepairTypeId();
        Map<EquipmentType, Map<EquipmentSubType, Map<EquipmentInRepairData.CompositeKey, List<EquipmentInRepairData>>>> grouped =
                equipmentInRepairRepository.findAllGrouped(repairTypeId, equipmentTypeIdsFinal);
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> result = new HashMap<>();
        grouped.forEach((equipmentType, subTypeMap) -> subTypeMap
                .forEach((subType, compositeKeyMap) -> compositeKeyMap
                        .forEach((key, equipmentInRepairDataList) -> buildLaborDistributionMap(result,
                                                                                               equipmentType,
                                                                                               subType,
                                                                                               equipmentInRepairDataList))));
        return result;
    }

    private Long getDefaultRepairTypeId() {
        return repairTypeRepository
                .findByName(LaborInputDistributionServiceImpl.REPAIR_TYPE.getName())
                .orElseThrow(() -> new NotFoundException("Тип ремонта не найден"))
                .getId();
    }

    private EquipmentInRepair buildEquipmentInRepair(EquipmentPerBase equipmentPerBase,
                                                     WorkhoursDistributionInterval interval,
                                                     double count,
                                                     double laborInput) {
        return new EquipmentInRepair(
                new EquipmentInRepairId(equipmentPerBase.getBase().getId(),
                                        equipmentPerBase.getEquipment().getId(),
                                        interval.getId()),
                equipmentPerBase.getBase(),
                equipmentPerBase.getEquipment(),
                interval,
                count,
                laborInput);
    }


    private EquipmentInRepair calculateEquipmentInRepair(EquipmentPerBase equipmentPerBase,
                                                         double avgDailyFailure,
                                                         int standardLaborInput,
                                                         WorkhoursDistributionInterval interval) {
        double count = calculationService.calculateEquipmentRequiringRepair(interval.getUpperBound(),
                                                                            interval.getLowerBound(),
                                                                            avgDailyFailure,
                                                                            standardLaborInput);
        double laborInput = calculationService.calculateEquipmentRepairComplexity(count,
                                                                                  interval.getUpperBound());
        return buildEquipmentInRepair(equipmentPerBase, interval, count, laborInput);
    }

    private Stream<EquipmentInRepair> calculateEquipmentLaborInputDistribution(
            Pair<EquipmentPerBase, Integer> equipmentPerBaseAndLaborInput) {
        EquipmentPerBase equipmentPerBase = equipmentPerBaseAndLaborInput.getLeft();
        double avgDailyFailure =
                calculationService.calculateEquipmentFailureAmount(equipmentPerBase.getAmount(),
                                                                   equipmentPerBase.getIntensity(),
                                                                   2.2);
        int standardLaborInput = equipmentPerBaseAndLaborInput.getRight();
        return StreamSupport.stream(workhoursDistributionIntervalRepository.findAll().spliterator(), false)
                            .map(interval -> calculateEquipmentInRepair(equipmentPerBase,
                                                                        avgDailyFailure,
                                                                        standardLaborInput,
                                                                        interval))
                            .filter(eir -> eir.getCount() > 0 || eir.getAvgLaborInput() > 0);
    }

    private List<EquipmentInRepair> calculateAndBuildEquipmentInRepair(List<Pair<EquipmentPerBase, Integer>> epbs) {
        return epbs.stream().flatMap(this::calculateEquipmentLaborInputDistribution).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateLaborInputDistribution() {
        List<Pair<EquipmentPerBase, Integer>> equipmentPerBases =
                equipmentPerBaseRepository.findAllWithLaborInput(getDefaultRepairTypeId());
        List<EquipmentInRepair> calculated = calculateAndBuildEquipmentInRepair(equipmentPerBases);
        equipmentInRepairRepository.deleteAll();
        equipmentInRepairRepository.saveAll(calculated);
    }

    @Override
    @Transactional
    public void updateLaborInputDistribution(Long baseId) {
        List<Pair<EquipmentPerBase, Integer>> equipmentPerBases =
                equipmentPerBaseRepository.findAllWithLaborInput(getDefaultRepairTypeId());
        List<EquipmentInRepair> calculated = calculateAndBuildEquipmentInRepair(equipmentPerBases);
        Iterable<EquipmentInRepair> existing = equipmentInRepairRepository.findByBaseId(baseId);
        equipmentInRepairRepository.deleteAll(existing);
        equipmentInRepairRepository.saveAll(calculated);
    }
}
