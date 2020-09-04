package va.rit.teho.controller;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.entity.*;
import va.rit.teho.enums.RestorationTypeEnum;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LaborInputDistributionController.class)
@ContextConfiguration(classes = TestRunner.class)
public class LaborInputDistributionControllerTest extends ControllerTest {

    @Test
    public void testGetDistributionIntervals() throws Exception {
        WorkhoursDistributionInterval interval =
                new WorkhoursDistributionInterval(1, 5, new RestorationType(RestorationTypeEnum.OPERATIONAL.getName()));
        interval.setId(12L);
        List<WorkhoursDistributionInterval> intervals = Collections.singletonList(interval);
        when(laborInputDistributionService.getDistributionIntervals()).thenReturn(intervals);

        mockMvc.perform(get("/labor-distribution/intervals"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()", is(intervals.size())))
               .andExpect(jsonPath("$[0].key", is(interval.getId().intValue())))
               .andExpect(jsonPath("$[0].from", is(interval.getLowerBound())))
               .andExpect(jsonPath("$[0].to", is(interval.getUpperBound())));
    }

    @Test
    public void testGetDistributionData() throws Exception {
        EquipmentType equipmentType = new EquipmentType("s", "f");
        EquipmentSubType equipmentSubType = new EquipmentSubType("s", "f", equipmentType);
        Long intervalKey = 3L;
        EquipmentLaborInputDistribution elid =
                new EquipmentLaborInputDistribution("b-n", equipmentType, equipmentSubType, "eq-name",
                                                    10.1,
                                                    3,
                                                    Collections.singletonMap(intervalKey,
                                                                             new CountAndLaborInput(2.2, 5.1)),
                                                    123.2);
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> equipmentTypeMap =
                Collections.singletonMap(equipmentType, Collections.singletonMap(equipmentSubType,
                                                                                 Collections.singletonList(elid)));
        when(laborInputDistributionService.getLaborInputDistribution(null)).thenReturn(equipmentTypeMap);

        mockMvc.perform(get("/labor-distribution"))
               .andExpect(jsonPath("$.size()", is(1)))
               .andExpect(jsonPath("$[0].type.shortName", is(equipmentType.getShortName())))
               .andExpect(jsonPath("$[0].type.fullName", is(equipmentType.getFullName())))
               .andExpect(jsonPath("$[0].subTypeDistribution.size()", is(1)))
               .andExpect(jsonPath("$[0].subTypeDistribution[0].subType.shortName",
                                   is(equipmentSubType.getShortName())))
               .andExpect(jsonPath("$[0].subTypeDistribution[0].subType.fullName", is(equipmentSubType.getFullName())))
               .andExpect(jsonPath("$[0].subTypeDistribution[0].equipmentDistribution.size()", is(1)))
               .andExpect(jsonPath("$[0].subTypeDistribution[0].equipmentDistribution[0].baseName",
                                   is(elid.getBaseName())))
               .andExpect(jsonPath("$[0].subTypeDistribution[0].equipmentDistribution[0].equipmentName",
                                   is(elid.getEquipmentName())))
               .andExpect(jsonPath("$[0].subTypeDistribution[0].equipmentDistribution[0].avgDailyFailure",
                                   is(elid.getAvgDailyFailure())))
               .andExpect(jsonPath("$[0].subTypeDistribution[0].equipmentDistribution[0].standardLaborInput",
                                   is(elid.getStandardLaborInput())))
               .andExpect(jsonPath("$[0].subTypeDistribution[0].equipmentDistribution[0].totalLaborInput",
                                   is(elid.getTotalRepairComplexity())))
               .andExpect(jsonPath("$[0].subTypeDistribution[0].equipmentDistribution[0].countAndLaborInputs.size()",
                                   is(elid.getIntervalCountAndLaborInputMap().size())))
               .andExpect(jsonPath("$[0].subTypeDistribution[0].equipmentDistribution[0].countAndLaborInputs[0].key",
                                   is(intervalKey.intValue())))
               .andExpect(jsonPath("$[0].subTypeDistribution[0].equipmentDistribution[0].countAndLaborInputs[0].count",
                                   is(elid.getIntervalCountAndLaborInputMap().get(intervalKey).getCount())))
               .andExpect(jsonPath(
                       "$[0].subTypeDistribution[0].equipmentDistribution[0].countAndLaborInputs[0].laborInput",
                       is(elid.getIntervalCountAndLaborInputMap().get(intervalKey).getLaborInput())));
    }

    @Test
    public void testUpdateDistributionData() throws Exception {
        mockMvc.perform(post("/labor-distribution")).andExpect(status().isAccepted());

        verify(laborInputDistributionService).updateLaborInputDistribution();
    }

    @Test
    public void testUpdateDistributionDataPerType() throws Exception {
        Long typeId = 2L;
        mockMvc.perform(post("/labor-distribution/type/{id}", typeId)).andExpect(status().isAccepted());

        verify(laborInputDistributionService).updateLaborInputDistributionPerEquipmentType(typeId);
    }

    @Test
    public void testUpdateDistributionDataPerSubType() throws Exception {
        Long typeId = 3L;
        mockMvc.perform(post("/labor-distribution/subtype/{id}", typeId)).andExpect(status().isAccepted());

        verify(laborInputDistributionService).updateLaborInputDistributionPerEquipmentSubType(typeId);
    }

    @Test
    public void testUpdateDistributionDataPerBase() throws Exception {
        Long baseId = 5L;
        mockMvc.perform(post("/labor-distribution/base/{id}", baseId)).andExpect(status().isAccepted());

        verify(laborInputDistributionService).updateLaborInputDistributionPerBase(baseId);
    }
}
