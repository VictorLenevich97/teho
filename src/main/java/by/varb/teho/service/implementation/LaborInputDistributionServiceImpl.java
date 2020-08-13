package by.varb.teho.service.implementation;

import by.varb.teho.entity.*;
import by.varb.teho.enums.RepairTypeEnum;
import by.varb.teho.exception.RepairTypeLaborInputNotFoundException;
import by.varb.teho.repository.EquipmentInRepairRepository;
import by.varb.teho.repository.WorkhoursDistributionIntervalRepository;
import by.varb.teho.service.BaseService;
import by.varb.teho.service.CalculationService;
import by.varb.teho.service.LaborInputDistributionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LaborInputDistributionServiceImpl implements LaborInputDistributionService {
    private static final RepairTypeEnum REPAIR_TYPE = RepairTypeEnum.CURRENT_REPAIR;
    private final BaseService baseService;
    private final WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository;
    private final CalculationService calculationService;
    private final EquipmentInRepairRepository equipmentInRepairRepository;

    public LaborInputDistributionServiceImpl(
            BaseService baseService,
            WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository,
            CalculationService calculationService, EquipmentInRepairRepository equipmentInRepairRepository) {
        this.baseService = baseService;
        this.workhoursDistributionIntervalRepository = workhoursDistributionIntervalRepository;
        this.calculationService = calculationService;
        this.equipmentInRepairRepository = equipmentInRepairRepository;
    }

    @Override
    //TODO: Simplify
    public Map<EquipmentType, List<EquipmentLaborInputDistribution>> getLaborInputDistribution() {
        List<EquipmentInRepair> equipmentInRepair = (List<EquipmentInRepair>) equipmentInRepairRepository.findAll();
        Map<EquipmentType, Map<Long, Map<Long, List<EquipmentLaborInputDistribution.Builder>>>> temporalResult = new HashMap<>();
        for (EquipmentInRepair eir : equipmentInRepair) {
            Equipment eirEquipment = eir.getEquipment();
            List<EquipmentLaborInputDistribution.Builder> distributionList =
                    temporalResult
                            .computeIfAbsent(eirEquipment.getEquipmentType(), et -> new HashMap<>())
                            .computeIfAbsent(eir.getBase().getId(), bid -> new HashMap<>())
                            .computeIfAbsent(eir.getEquipment().getId(), id -> new ArrayList<>());
            EquipmentLaborInputDistribution.Builder equipmentLaborInputDistribution =
                    getEquipmentLaborInputDistribution(eir);
            distributionList.add(equipmentLaborInputDistribution);
        }
        Map<EquipmentType, List<EquipmentLaborInputDistribution>> result = new HashMap<>();
        for (Map.Entry<EquipmentType, Map<Long, Map<Long, List<EquipmentLaborInputDistribution.Builder>>>> equipmentTypeMapEntry : temporalResult
                .entrySet()) {
            List<EquipmentLaborInputDistribution> distributionList = result.computeIfAbsent(equipmentTypeMapEntry.getKey(),
                                                                                            et -> new ArrayList<>());
            for (Map.Entry<Long, Map<Long, List<EquipmentLaborInputDistribution.Builder>>> baseMapEntry : equipmentTypeMapEntry
                    .getValue()
                    .entrySet()) {
                for (Map.Entry<Long, List<EquipmentLaborInputDistribution.Builder>> equipmentMapEntry : baseMapEntry
                        .getValue()
                        .entrySet()) {
                    Optional<EquipmentLaborInputDistribution> equipmentLaborInputDistribution = populateWithLaborInputMapAndTotal(
                            equipmentMapEntry);
                    equipmentLaborInputDistribution.ifPresent(distributionList::add);
                }
            }
        }
        return result;
    }

    private Optional<EquipmentLaborInputDistribution> populateWithLaborInputMapAndTotal(Map.Entry<Long, List<EquipmentLaborInputDistribution.Builder>> equipmentMapEntry) {
        Optional<EquipmentLaborInputDistribution.Builder> summarized =
                equipmentMapEntry
                        .getValue()
                        .stream()
                        .reduce((l, r) -> {
                            Map<WorkhoursDistributionInterval, EquipmentLaborInputDistribution.CountAndLaborInput> m =
                                    new HashMap<>(l.getIntervalCountAndLaborInputMap());
                            m.putAll(r.getIntervalCountAndLaborInputMap());
                            return l.intervalCountAndLaborInputMap(m);
                        });
        return summarized
                .map(elid -> elid.totalRepairComplexity(
                        elid
                                .getIntervalCountAndLaborInputMap()
                                .values()
                                .stream()
                                .mapToDouble(EquipmentLaborInputDistribution.CountAndLaborInput::getLaborInput)
                                .sum())
                                 .build());
    }

    private EquipmentLaborInputDistribution.Builder getEquipmentLaborInputDistribution(EquipmentInRepair eir) {
        Equipment eirEquipment = eir.getEquipment();
        return EquipmentLaborInputDistribution
                .builder()
                .baseName(eir.getBase().getFullName())
                .equipmentType(eirEquipment.getEquipmentType())
                .equipment(eirEquipment)
                .avgDailyFailure(eir.getCount())
                .standardLaborInput(getStandardLaborInput(eirEquipment))
                .intervalCountAndLaborInputMap(
                        Collections.singletonMap(
                                eir.getWorkhoursDistributionInterval(),
                                new EquipmentLaborInputDistribution.CountAndLaborInput(eir.getCount(),
                                                                                       eir.getAvgLaborInput())));
    }

    @Override
    @Transactional
    public void updateLaborInputDistribution() {
        List<Base> bases = baseService.list();
        List<EquipmentInRepair> calculated = getEquipmentInRepairs(bases);
        equipmentInRepairRepository.deleteAll();
        equipmentInRepairRepository.saveAll(calculated);
    }

    private List<EquipmentInRepair> getEquipmentInRepairs(List<Base> bases) {
        List<WorkhoursDistributionInterval> distributionIntervals =
                (List<WorkhoursDistributionInterval>) workhoursDistributionIntervalRepository.findAll();
        List<EquipmentInRepair> calculated = new ArrayList<>();
        for (Base base : bases) {
            for (EquipmentPerBase equipmentPerBase : base.getEquipmentPerBases()) {
                Map<WorkhoursDistributionInterval, EquipmentLaborInputDistribution.CountAndLaborInput> equipmentLaborInputDistribution =
                        calculateEquipmentLaborInputDistribution(distributionIntervals, equipmentPerBase);
                calculated.addAll(filterAndMap(equipmentPerBase, equipmentLaborInputDistribution));
            }
        }
        return calculated;
    }

    @Override
    @Transactional
    public void updateLaborInputDistribution(Long baseId) {
        List<Base> bases = Collections.singletonList(baseService.get(baseId));
        List<EquipmentInRepair> calculated = getEquipmentInRepairs(bases);
        Iterable<EquipmentInRepair> existing = equipmentInRepairRepository.findByBaseId(baseId);
        equipmentInRepairRepository.deleteAll(existing);
        equipmentInRepairRepository.saveAll(calculated);
    }

    private List<EquipmentInRepair> filterAndMap(
            EquipmentPerBase equipmentPerBase,
            Map<WorkhoursDistributionInterval,
                    EquipmentLaborInputDistribution.CountAndLaborInput> equipmentLaborInputDistribution) {
        return equipmentLaborInputDistribution
                .entrySet()
                .stream()
                .filter(e -> e.getValue().getCount() > 0 || e.getValue().getLaborInput() > 0)
                .map(entry ->
                             new EquipmentInRepair(
                                     new EquipmentInRepairId(
                                             equipmentPerBase.getBase().getId(),
                                             equipmentPerBase.getEquipment().getId(),
                                             entry.getKey().getId()),
                                     equipmentPerBase.getBase(),
                                     equipmentPerBase.getEquipment(),
                                     entry.getKey(),
                                     entry.getValue().getCount(),
                                     entry.getValue().getLaborInput()))
                .collect(Collectors.toList());
    }

    private Map<WorkhoursDistributionInterval, EquipmentLaborInputDistribution.CountAndLaborInput> calculateEquipmentLaborInputDistribution(
            List<WorkhoursDistributionInterval> distributionIntervals,
            EquipmentPerBase equipmentPerBase) {
        Equipment equipment = equipmentPerBase.getEquipment();
        double avgDailyFailure =
                calculationService.calculateEquipmentFailureAmount(equipmentPerBase.getAmount(),
                                                                   equipmentPerBase.getIntensity(),
                                                                   2.2);
        int standardLaborInput = getStandardLaborInput(equipment);
        return mapEquipmentCountAndComplexity(distributionIntervals, avgDailyFailure, standardLaborInput);
    }

    private int getStandardLaborInput(Equipment equipment) {
        return equipment
                .getLaborInputPerTypes()
                .stream()
                .filter(lipt -> lipt.getRepairType()
                                    .getName()
                                    .equals(LaborInputDistributionServiceImpl.REPAIR_TYPE.getName()))
                .findFirst()
                .orElseThrow(() -> new RepairTypeLaborInputNotFoundException(LaborInputDistributionServiceImpl.REPAIR_TYPE,
                                                                             equipment))
                .getAmount();
    }

    private Map<WorkhoursDistributionInterval, EquipmentLaborInputDistribution.CountAndLaborInput> mapEquipmentCountAndComplexity(
            List<WorkhoursDistributionInterval> intervals,
            double avgDailyFailure,
            int standardLaborInput) {
        Map<WorkhoursDistributionInterval, EquipmentLaborInputDistribution.CountAndLaborInput> result = new HashMap<>();
        for (WorkhoursDistributionInterval interval : intervals) {
            double count = calculationService.calculateEquipmentRequiringRepair(interval.getUpperBound(),
                                                                                interval.getLowerBound(),
                                                                                avgDailyFailure,
                                                                                standardLaborInput);
            double laborInput = calculationService.calculateEquipmentRepairComplexity(count, interval.getUpperBound());
            result.put(interval, new EquipmentLaborInputDistribution.CountAndLaborInput(count, laborInput));
        }
        return result;
    }
}
