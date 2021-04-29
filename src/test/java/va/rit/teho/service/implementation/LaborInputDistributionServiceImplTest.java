package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.labordistribution.combined.CountAndLaborInput;
import va.rit.teho.entity.labordistribution.combined.CountAndLaborInputCombinedData;
import va.rit.teho.entity.labordistribution.combined.EquipmentLaborInputDistribution;
import va.rit.teho.entity.labordistribution.combined.LaborDistributionData;
import va.rit.teho.repository.equipment.EquipmentPerFormationFailureIntensityRepository;
import va.rit.teho.repository.labordistribution.LaborDistributionRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.implementation.labordistribution.LaborInputDistributionServiceImpl;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;
import va.rit.teho.service.labordistribution.WorkhoursDistributionIntervalService;

import java.util.*;

import static org.mockito.Mockito.*;

public class LaborInputDistributionServiceImplTest {

    private final CalculationService calculationService = Mockito.mock(CalculationService.class);
    private final EquipmentPerFormationService equipmentPerFormationService = Mockito.mock(EquipmentPerFormationService.class);
    private final RepairTypeService repairTypeService = Mockito.mock(RepairTypeService.class);
    private final LaborDistributionRepository laborDistributionRepository = Mockito.mock(LaborDistributionRepository.class);
    private final EquipmentPerFormationFailureIntensityRepository equipmentPerFormationFailureIntensityRepository = Mockito.mock(EquipmentPerFormationFailureIntensityRepository.class);
    private final WorkhoursDistributionIntervalService workhoursDistributionIntervalService = Mockito.mock(WorkhoursDistributionIntervalService.class);

    private final LaborInputDistributionService laborInputDistributionService =
            new LaborInputDistributionServiceImpl(
                    workhoursDistributionIntervalService,
                    calculationService,
                    equipmentPerFormationService,
                    repairTypeService,
                    laborDistributionRepository,
                    equipmentPerFormationFailureIntensityRepository);

    @Test
    public void testGetLaborInputDistribution() {
        UUID sessionId = UUID.randomUUID();
        Long repairTypeId = 12L;
        Long stageId = 13L;

        Formation formation = new Formation(1L, "short", "full");
        EquipmentType equipmentType = new EquipmentType("shortET", "fullET");
        Equipment equipment = new Equipment(2L, "eqName", equipmentType);
        EquipmentPerFormation equipmentPerFormation = new EquipmentPerFormation(formation, equipment, 10L);
        RepairType repairType = new RepairType(repairTypeId, "full", "short", true, false, false);

        double avgDailyFailure = 123.123;
        int standardLaborInput = 100;
        double count = 2.2;
        long intervalId = 1L;
        double avgLaborInput = 22.2;
        LaborDistributionData laborDistributionData1 = new LaborDistributionData(equipmentPerFormation, repairType, standardLaborInput, intervalId, count, avgLaborInput, avgDailyFailure);

        long secondIntervalId = 2L;
        double secondCount = 3.3;
        double secondAvgLaborInput = 33.3;

        LaborDistributionData laborDistributionData2 = new LaborDistributionData(equipmentPerFormation, repairType, standardLaborInput, secondIntervalId, secondCount, secondAvgLaborInput, avgDailyFailure);

        Map<EquipmentPerFormation, List<LaborDistributionData>> equipmentPerFormationListMap =
                Collections.singletonMap(equipmentPerFormation, Arrays.asList(laborDistributionData1, laborDistributionData2));

        Map<EquipmentType, Map<EquipmentPerFormation, List<LaborDistributionData>>> equipmentTypeMap =
                Collections.singletonMap(equipmentType, equipmentPerFormationListMap);

        when(laborDistributionRepository.findAllGrouped(sessionId, repairTypeId, stageId, null)).thenReturn(equipmentTypeMap);

        Map<Long, CountAndLaborInput> laborInputMap = new HashMap<>();
        laborInputMap.put(intervalId, new CountAndLaborInput(count, avgLaborInput));
        laborInputMap.put(secondIntervalId, new CountAndLaborInput(secondCount, secondAvgLaborInput));

        EquipmentLaborInputDistribution expectedLID =
                EquipmentLaborInputDistribution
                        .builder()
                        .formationName(formation.getFullName())
                        .equipmentName(equipment.getName())
                        .avgDailyFailure(avgDailyFailure)
                        .standardLaborInput(standardLaborInput)
                        .countAndLaborInputCombinedData(Collections.singletonMap(repairType,
                                new CountAndLaborInputCombinedData(laborInputMap)))
                        .totalRepairComplexity(
                                laborInputMap.values().stream().mapToDouble(CountAndLaborInput::getLaborInput).sum())
                        .build();

        Map<EquipmentType, List<EquipmentLaborInputDistribution>> expectedResult = Collections.singletonMap(equipmentType, Collections.singletonList(expectedLID));

        Map<EquipmentType, List<EquipmentLaborInputDistribution>> result = laborInputDistributionService.getLaborInputDistribution(sessionId, repairTypeId, stageId, null);

        Assertions.assertEquals(expectedResult, result);

        verify(laborDistributionRepository).findAllGrouped(sessionId, repairTypeId, stageId, null);
        verifyNoMoreInteractions(laborDistributionRepository);
    }
}
