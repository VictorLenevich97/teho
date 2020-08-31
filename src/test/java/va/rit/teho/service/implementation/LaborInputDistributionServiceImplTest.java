package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.*;
import va.rit.teho.enums.RepairTypeEnum;
import va.rit.teho.enums.RestorationTypeEnum;
import va.rit.teho.model.Pair;
import va.rit.teho.repository.*;
import va.rit.teho.service.CalculationService;
import va.rit.teho.service.LaborInputDistributionService;
import va.rit.teho.service.implementation.LaborInputDistributionServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LaborInputDistributionServiceImplTest {

    private final CalculationService calculationService = Mockito.mock(CalculationService.class);

    private final EquipmentTypeRepository equipmentTypeRepository = Mockito.mock(EquipmentTypeRepository.class);
    private final EquipmentPerBaseRepository equipmentPerBaseRepository = Mockito.mock(EquipmentPerBaseRepository.class);
    private final EquipmentInRepairRepository equipmentInRepairRepository = Mockito.mock(EquipmentInRepairRepository.class);
    private final RepairTypeRepository repairTypeRepository = Mockito.mock(RepairTypeRepository.class);
    private final WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository =
            Mockito.mock(WorkhoursDistributionIntervalRepository.class);

    private final LaborInputDistributionService laborInputDistributionService =
            new LaborInputDistributionServiceImpl(
                    equipmentTypeRepository,
                    equipmentPerBaseRepository,
                    workhoursDistributionIntervalRepository,
                    calculationService,
                    equipmentInRepairRepository,
                    repairTypeRepository);

    @Test
    public void testGetDistributionIntervals() {
        List<WorkhoursDistributionInterval> intervals =
                Collections.singletonList(
                        new WorkhoursDistributionInterval(
                                1,
                                10,
                                new RestorationType(RestorationTypeEnum.TACTICAL.getName())));
        when(workhoursDistributionIntervalRepository.findAll()).thenReturn(intervals);

        Assertions.assertEquals(intervals, laborInputDistributionService.getDistributionIntervals());
    }

    @Test
    public void testGetLaborInputDistribution() {
        RepairType repairType = new RepairType(1L);
        when(repairTypeRepository.findByName(RepairTypeEnum.CURRENT_REPAIR.getName()))
                .thenReturn(Optional.of(repairType));
        EquipmentType equipmentType = new EquipmentType("s", "f");
        equipmentType.setId(2L);
        EquipmentSubType equipmentSubType = new EquipmentSubType("short", "full", equipmentType);
        Equipment e = new Equipment("eq", equipmentSubType);
        e.setId(3L);
        when(equipmentTypeRepository.findAll()).thenReturn(Collections.singletonList(equipmentType));
        EquipmentInRepairData equipmentInRepairData = new EquipmentInRepairData(equipmentSubType,
                                                                                "basename",
                                                                                e.getId(),
                                                                                e.getName(),
                                                                                100,
                                                                                1L,
                                                                                12.1,
                                                                                120.2);
        Map<EquipmentType, Map<EquipmentSubType, Map<EquipmentInRepairData.CompositeKey, List<EquipmentInRepairData>>>> grouped =
                Collections.singletonMap(equipmentType,
                                         Collections.singletonMap(equipmentSubType,
                                                                  Collections.singletonMap(new EquipmentInRepairData.CompositeKey(
                                                                                                   equipmentSubType,
                                                                                                   e.getId()),
                                                                                           Collections.singletonList(
                                                                                                   equipmentInRepairData))));
        when(equipmentInRepairRepository.findAllGrouped(repairType.getId(),
                                                        Collections.singletonList(equipmentType.getId()))).thenReturn(
                grouped);
        EquipmentLaborInputDistribution elid =
                EquipmentLaborInputDistribution.builder()
                                               .avgDailyFailure(equipmentInRepairData.getCount())
                                               .baseName(equipmentInRepairData.getBaseName())
                                               .equipmentName(e.getName())
                                               .equipmentSubType(equipmentSubType)
                                               .equipmentType(equipmentType)
                                               .intervalCountAndLaborInputMap(Collections.singletonMap(
                                                       equipmentInRepairData.getIntervalId(),
                                                       new CountAndLaborInput(equipmentInRepairData.getCount(),
                                                                              equipmentInRepairData.getAvgLaborInput())))
                                               .standardLaborInput(equipmentInRepairData.getLaborInput())
                                               .totalRepairComplexity(equipmentInRepairData.getAvgLaborInput())
                                               .build();
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> result = Collections.singletonMap(
                equipmentType,
                Collections.singletonMap(equipmentSubType, Collections.singletonList(elid)));
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> laborInputDistribution = laborInputDistributionService
                .getLaborInputDistribution(Collections.emptyList());
        Assertions.assertEquals(result, laborInputDistribution);
    }

    @Test
    public void testUpdateLaborInputDistribution() {
        RepairType repairType = new RepairType(1L);
        when(repairTypeRepository.findByName(RepairTypeEnum.CURRENT_REPAIR.getName()))
                .thenReturn(Optional.of(repairType));
        Base base = new Base("s", "f");
        base.setId(3L);
        Equipment e = new Equipment("eqName", new EquipmentSubType("s", "f", new EquipmentType("s", "f")));
        e.setId(5L);
        EquipmentPerBase equipmentPerBase = new EquipmentPerBase(base, e, 50, 20);
        Pair<EquipmentPerBase, Integer> equipmentPerBaseWithLaborInput = Pair.of(equipmentPerBase, 120);
        when(equipmentPerBaseRepository.findAllWithLaborInput(repairType.getId()))
                .thenReturn(Collections.singletonList(equipmentPerBaseWithLaborInput));
        double avgDailyFailure = 123.12;
        when(calculationService.calculateEquipmentFailureAmount(equipmentPerBase.getAmount(),
                                                                equipmentPerBase.getIntensity(),
                                                                2.2)).thenReturn(avgDailyFailure);

        WorkhoursDistributionInterval interval = new WorkhoursDistributionInterval(
                1,
                10,
                new RestorationType(RestorationTypeEnum.TACTICAL.getName()));
        interval.setId(2L);
        List<WorkhoursDistributionInterval> intervals =
                Collections.singletonList(
                        interval);
        when(workhoursDistributionIntervalRepository.findAll()).thenReturn(intervals);

        double count = 1.24;

        when(calculationService.calculateEquipmentRequiringRepair(interval.getUpperBound(),
                                                                  interval.getLowerBound(),
                                                                  avgDailyFailure,
                                                                  equipmentPerBaseWithLaborInput.getRight())).thenReturn(
                count);

        double laborInput = 12.12;

        when(calculationService.calculateEquipmentRepairComplexity(count, interval.getUpperBound())).thenReturn(
                laborInput);

        EquipmentInRepair equipmentInRepair = new EquipmentInRepair(
                new EquipmentInRepairId(base.getId(), e.getId(), interval.getId()),
                base,
                e,
                interval,
                count,
                laborInput);

        laborInputDistributionService.updateLaborInputDistribution();

        verify(equipmentInRepairRepository).saveAll(Collections.singletonList(equipmentInRepair));
    }
}
