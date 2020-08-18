package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.*;
import va.rit.teho.enums.RepairTypeEnum;
import va.rit.teho.exception.RepairTypeLaborInputNotFoundException;
import va.rit.teho.model.Pair;
import va.rit.teho.repository.EquipmentInRepairRepository;
import va.rit.teho.repository.WorkhoursDistributionIntervalRepository;
import va.rit.teho.service.BaseService;
import va.rit.teho.service.CalculationService;
import va.rit.teho.service.LaborInputDistributionService;

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
    public Map<EquipmentType, List<EquipmentLaborInputDistribution>> getLaborInputDistribution() {
        Map<Pair<Long, Long>, List<EquipmentInRepair>> equipmentInRepair = equipmentInRepairRepository.findAllGroupedByBaseAndEquipment();
        return
                equipmentInRepair
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                                  eirs -> eirs
                                                          .getValue()
                                                          .stream()
                                                          .reduce(Pair.of(new EquipmentInRepair(), new HashMap<>()),
                                                                  this::getEquipmentInRepairHashMapPair,
                                                                  (l, r) -> l)))
                        .values()
                        .stream()
                        .map(this::getEquipmentLaborInputDistribution)
                        .collect(Collectors.groupingBy(EquipmentLaborInputDistribution::getEquipmentType));
    }

    private Pair<EquipmentInRepair, HashMap<WorkhoursDistributionInterval, Pair<Double, Double>>> getEquipmentInRepairHashMapPair(
            Pair<EquipmentInRepair, HashMap<WorkhoursDistributionInterval, Pair<Double, Double>>> p,
            EquipmentInRepair eir) {
        p.getRight().put(eir.getWorkhoursDistributionInterval(), Pair.of(eir.getCount(), eir.getAvgLaborInput()));
        return Pair.of(eir, p.getRight());
    }

    private EquipmentLaborInputDistribution getEquipmentLaborInputDistribution(
            Pair<EquipmentInRepair, HashMap<WorkhoursDistributionInterval, Pair<Double, Double>>> pair) {
        EquipmentInRepair eir = pair.getLeft();
        Equipment eirEquipment = eir.getEquipment();
        return EquipmentLaborInputDistribution
                .builder()
                .base(eir.getBase())
                .equipmentType(eirEquipment.getEquipmentType())
                .equipment(eirEquipment)
                .avgDailyFailure(eir.getCount())
                .standardLaborInput(getStandardLaborInput(eirEquipment))
                .intervalCountAndLaborInputMap(pair.getRight().entrySet().stream().collect(
                        Collectors.toMap(Map.Entry::getKey, e -> new CountAndLaborInput(e.getValue().getLeft(),
                                                                                        e.getValue().getRight()))))
                .build();
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
                Map<WorkhoursDistributionInterval, CountAndLaborInput> equipmentLaborInputDistribution =
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
                    CountAndLaborInput> equipmentLaborInputDistribution) {
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

    private Map<WorkhoursDistributionInterval, CountAndLaborInput> calculateEquipmentLaborInputDistribution(
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
                .filter(lipt -> lipt
                        .getRepairType()
                        .getName()
                        .equals(LaborInputDistributionServiceImpl.REPAIR_TYPE.getName()))
                .findFirst()
                .orElseThrow(() -> new RepairTypeLaborInputNotFoundException(LaborInputDistributionServiceImpl.REPAIR_TYPE,
                                                                             equipment))
                .getAmount();
    }

    private Map<WorkhoursDistributionInterval, CountAndLaborInput> mapEquipmentCountAndComplexity(
            List<WorkhoursDistributionInterval> intervals,
            double avgDailyFailure,
            int standardLaborInput) {
        Map<WorkhoursDistributionInterval, CountAndLaborInput> result = new HashMap<>();
        for (WorkhoursDistributionInterval interval : intervals) {
            double count = calculationService.calculateEquipmentRequiringRepair(interval.getUpperBound(),
                                                                                interval.getLowerBound(),
                                                                                avgDailyFailure,
                                                                                standardLaborInput);
            double laborInput = calculationService.calculateEquipmentRepairComplexity(count, interval.getUpperBound());
            result.put(interval, new CountAndLaborInput(count, laborInput));
        }
        return result;
    }
}
