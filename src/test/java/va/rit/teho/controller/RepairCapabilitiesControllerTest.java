//package va.rit.teho.controller;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.ContextConfiguration;
//import va.rit.teho.TestRunner;
//import va.rit.teho.entity.equipment.Equipment;
//import va.rit.teho.entity.repairstation.RepairStation;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = RepairCapabilitiesController.class)
//@ContextConfiguration(classes = TestRunner.class)
//public class RepairCapabilitiesControllerTest extends ControllerTest {
//
//    @Test
//    public void testGetCalculatedRepairCapabilities() throws Exception {
//        RepairStation rs = repairStation(1L, "rsname", 2L, "typeName", 1, 5, null);
//        Equipment e = equipment(3L, "eq-name");
//        Double amount = 12.34;
//        when(repairCapabilitiesService.getCalculatedRepairCapabilities(null,
//                                                                       null,
//                                                                       null,
//                                                                       null)).thenReturn(Collections.singletonMap(
//                rs,
//                Collections.singletonMap(e, amount)));
//
//        mockMvc.perform(get("/repair-capabilities"))
//               .andExpect(status().isOk())
//               .andExpect(jsonPath("$.size()", is(1)))
//               .andExpect(jsonPath("$[0].repairStationId", is(rs.getId().intValue())))
//               .andExpect(jsonPath("$[0].capabilities.size()", is(1)))
//               .andExpect(jsonPath("$[0].capabilities[0].id", is(e.getId().intValue())))
//               .andExpect(jsonPath("$[0].capabilities[0].capability", is(amount)));
//        verify(repairCapabilitiesService).getCalculatedRepairCapabilities(null, null, null, null);
//        verifyNoMoreInteractions(repairCapabilitiesService);
//    }
//
//    @Test
//    public void testGetCalculatedRepairCapabilitiesWithFilter() throws Exception {
//        List<Long> repairStationIds = Arrays.asList(1L, 2L);
//        RepairStation rs = repairStation(1L, "rsname", 2L, "typeName", 1, 5, null);
//        Equipment e = equipment(3L, "eq-name");
//        Double amount = 12.34;
//        when(repairCapabilitiesService.getCalculatedRepairCapabilities(repairStationIds, null, null, null)).thenReturn(
//                Collections.singletonMap(
//                        rs,
//                        Collections.singletonMap(e, amount)));
//
//        mockMvc.perform(get("/repair-capabilities?repairStationId=1,2"))
//               .andExpect(status().isOk())
//               .andExpect(jsonPath("$.size()", is(1)))
//               .andExpect(jsonPath("$[0].repairStationId", is(rs.getId().intValue())))
//               .andExpect(jsonPath("$[0].capabilities.size()", is(1)))
//               .andExpect(jsonPath("$[0].capabilities[0].id", is(e.getId().intValue())))
//               .andExpect(jsonPath("$[0].capabilities[0].capability", is(amount)));
//        verify(repairCapabilitiesService).getCalculatedRepairCapabilities(repairStationIds, null, null, null);
//        verifyNoMoreInteractions(repairCapabilitiesService);
//    }
//
//    @Test
//    public void testCalculateAndGet() throws Exception {
//        RepairStation rs = repairStation(1L, "rsname", 2L, "typeName", 1, 5, null);
//        Equipment e = equipment(3L, "eq-name");
//        Double amount = 12.34;
//        when(repairCapabilitiesService.getCalculatedRepairCapabilities(Collections.emptyList(),
//                                                                       Collections.emptyList(),
//                                                                       Collections.emptyList(),
//                                                                       Collections.emptyList())).thenReturn(Collections.singletonMap(
//                rs,
//                Collections.singletonMap(e, amount)));
//
//        mockMvc.perform(post("/repair-capabilities"))
//               .andExpect(status().isOk())
//               .andExpect(jsonPath("$.size()", is(1)))
//               .andExpect(jsonPath("$[0].repairStationId", is(rs.getId().intValue())))
//               .andExpect(jsonPath("$[0].capabilities.size()", is(1)))
//               .andExpect(jsonPath("$[0].capabilities[0].id", is(e.getId().intValue())))
//               .andExpect(jsonPath("$[0].capabilities[0].capability", is(amount)));
//
//        verify(repairCapabilitiesService).calculateAndUpdateRepairCapabilities();
//    }
//
//    @Test
//    public void testCalculateAndGetPerRepairStation() throws Exception {
//        RepairStation rs = repairStation(1L, "rsname", 2L, "typeName", 1, 5, null);
//        Equipment e = equipment(3L, "eq-name");
//        Double amount = 12.34;
//        when(repairCapabilitiesService.getCalculatedRepairCapabilities(Collections.singletonList(rs.getId()),
//                                                                       Collections.emptyList(),
//                                                                       Collections.emptyList(),
//                                                                       Collections.emptyList())).thenReturn(
//                Collections.singletonMap(
//                        rs,
//                        Collections.singletonMap(e, amount)));
//
//        mockMvc.perform(post("/repair-capabilities/repair-station/{id}", rs.getId()))
//               .andExpect(status().isAccepted())
//               .andExpect(jsonPath("$.repairStationId", is(rs.getId().intValue())))
//               .andExpect(jsonPath("$.capabilities.size()", is(1)))
//               .andExpect(jsonPath("$.capabilities[0].id", is(e.getId().intValue())))
//               .andExpect(jsonPath("$.capabilities[0].capability", is(amount)));
//
//        verify(repairCapabilitiesService).calculateAndUpdateRepairCapabilitiesPerStation(rs.getId());
//    }
//}
