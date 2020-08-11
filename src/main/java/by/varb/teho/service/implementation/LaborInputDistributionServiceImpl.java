package by.varb.teho.service.implementation;

import by.varb.teho.entity.*;
import by.varb.teho.enums.RepairTypeEnum;
import by.varb.teho.exception.RepairTypeLaborInputNotFoundException;
import by.varb.teho.repository.WorkhoursDistributionIntervalRepository;
import by.varb.teho.service.BaseService;
import by.varb.teho.service.CalculationService;
import by.varb.teho.service.LaborInputDistributionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LaborInputDistributionServiceImpl implements LaborInputDistributionService {
    private final BaseService baseService;
    private final WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository;
    private final CalculationService calculationService;

    public LaborInputDistributionServiceImpl(
            BaseService baseService,
            WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository,
            CalculationService calculationService) {
        this.baseService = baseService;
        this.workhoursDistributionIntervalRepository = workhoursDistributionIntervalRepository;
        this.calculationService = calculationService;
    }


    @Override
    public Map<EquipmentType, List<EquipmentLaborInputDistribution>> calculateLaborInputDistribution() {
        RepairTypeEnum repairType = RepairTypeEnum.CURRENT_REPAIR;
        List<Base> bases = baseService.list();
        List<WorkhoursDistributionInterval> distributionIntervals =
                (List<WorkhoursDistributionInterval>) workhoursDistributionIntervalRepository.findAll();
        Map<EquipmentType, List<EquipmentLaborInputDistribution>> result = new HashMap<>();
        for (Base base : bases) {
            for (EquipmentPerBase equipmentPerBase : base.getEquipmentPerBases()) {
                Equipment equipment = equipmentPerBase.getEquipment();
                List<EquipmentLaborInputDistribution> distributionList =
                        result.computeIfAbsent(equipment.getEquipmentType(), (et) -> new ArrayList<>());
                EquipmentLaborInputDistribution equipmentLaborInputDistribution =
                        getEquipmentLaborInputDistribution(repairType, distributionIntervals, base, equipmentPerBase, equipment);
                distributionList.add(equipmentLaborInputDistribution);
            }
        }
        return result;
    }

    private EquipmentLaborInputDistribution getEquipmentLaborInputDistribution(
            RepairTypeEnum repairType,
            List<WorkhoursDistributionInterval> distributionIntervals,
            Base base,
            EquipmentPerBase equipmentPerBase,
            Equipment equipment) {
        double avgDailyFailure =
                calculationService.calculateEquipmentFailureAmount(equipmentPerBase.getAmount(), equipmentPerBase.getIntensity(), 2.2);
        int standardLaborInput =
                equipment
                        .getLaborInputPerTypes()
                        .stream()
                        .filter(lipt -> lipt.getRepairType().getName().equals(repairType.getName()))
                        .findFirst()
                        .orElseThrow(() -> new RepairTypeLaborInputNotFoundException(repairType, equipment))
                        .getAmount();
        Map<WorkhoursDistributionInterval, EquipmentLaborInputDistribution.CountAndLaborInput> laborInputMap =
                mapEquipmentCountAndComplexity(distributionIntervals, avgDailyFailure, standardLaborInput);
        int totalRepairComplexity = laborInputMap.values().stream().mapToInt(EquipmentLaborInputDistribution.CountAndLaborInput::getLaborInput).sum();
        return new EquipmentLaborInputDistribution(
                base.getFullName(),
                equipment.getEquipmentType(),
                equipment,
                avgDailyFailure,
                standardLaborInput,
                laborInputMap,
                totalRepairComplexity);
    }

    private Map<WorkhoursDistributionInterval, EquipmentLaborInputDistribution.CountAndLaborInput> mapEquipmentCountAndComplexity(
            List<WorkhoursDistributionInterval> intervals,
            double avgDailyFailure,
            int standardLaborInput) {
        Map<WorkhoursDistributionInterval, EquipmentLaborInputDistribution.CountAndLaborInput> result = new HashMap<>();
        for (WorkhoursDistributionInterval interval : intervals) {
            int count = calculationService.calculateEquipmentRequiringRepair(interval.getUpperBound(), interval.getLowerBound(), avgDailyFailure, standardLaborInput);
            int laborInput = calculationService.calculateEquipmentRepairComplexity(count, interval.getUpperBound());
            result.put(interval, new EquipmentLaborInputDistribution.CountAndLaborInput(count, laborInput));
        }
        return result;
    }
}
