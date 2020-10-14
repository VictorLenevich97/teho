//package va.rit.teho.controller;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ContextConfiguration;
//import va.rit.teho.TestRunner;
//import va.rit.teho.dto.BaseDTO;
//import va.rit.teho.dto.EquipmentStaffDTO;
//import va.rit.teho.dto.RepairStationDTO;
//import va.rit.teho.dto.RepairStationTypeDTO;
//import va.rit.teho.entity.*;
//
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = RepairStationController.class)
//@ContextConfiguration(classes = TestRunner.class)
//public class RepairStationControllerTest extends ControllerTest {
//
//    @Test
//    public void testListRepairStations() throws Exception {
//        Base firstBase = base(1L, "first");
//        Base secondBase = base(2L, "second");
//        RepairStationType repairStationType1 = new RepairStationType("type1", 1, 5);
//        RepairStationType repairStationType2 = new RepairStationType("type2", 3, 7);
//        List<RepairStation> repairStationDTOList = Arrays.asList(
//                new RepairStation("first", repairStationType1, firstBase, 3),
//                new RepairStation("second", repairStationType2, secondBase, 5));
//
//        when(repairStationService.list(Collections.emptyList())).thenReturn(repairStationDTOList);
//
//        mockMvc.perform(get("/repair-station"))
//               .andExpect(status().isOk())
//               .andExpect(jsonPath("$.size()", is(repairStationDTOList.size())));
//    }
//
//    @Test
//    public void testGetRepairStationById() throws Exception {
//        Long repairStationId = 1L;
//        Base b = base(5L, "base");
//        RepairStation repairStation = repairStation(repairStationId, "repairStation", 11L, "typeName", 1, 5, b);
//
//        RepairStationEquipmentStaff repairStationEquipmentStaff = new RepairStationEquipmentStaff(null,
//                                                                                                  10,
//                                                                                                  3);
//        Equipment equipment = equipment(3L, "e");
//        repairStationEquipmentStaff.setEquipment(equipment);
//        List<RepairStationEquipmentStaff> staff = Collections.singletonList(repairStationEquipmentStaff);
//        when(repairStationService.get(repairStationId)).thenReturn(Pair.of(repairStation, staff));
//
//        mockMvc.perform(get("/repair-station/{id}", repairStationId))
//               .andExpect(status().isOk())
//               .andExpect(jsonPath("$.name", is(repairStation.getName())))
//               .andExpect(jsonPath("$.type.id", is(repairStation.getRepairStationType().getId().intValue())))
//               .andExpect(jsonPath("$.type.name", is(repairStation.getRepairStationType().getName())))
//               .andExpect(jsonPath("$.type.workingHoursMin",
//                                   is(repairStation.getRepairStationType().getWorkingHoursMin())))
//               .andExpect(jsonPath("$.type.workingHoursMax",
//                                   is(repairStation.getRepairStationType().getWorkingHoursMax())))
//               .andExpect(jsonPath("$.base.id", is(b.getId().intValue())))
//               .andExpect(jsonPath("$.amount", is(repairStation.getStationAmount())))
//               .andExpect(jsonPath("$.equipmentStaff.size()", is(staff.size())))
//               .andExpect(jsonPath("$.equipmentStaff[0].equipmentId", is(equipment.getId().intValue())))
//               .andExpect(jsonPath("$.equipmentStaff[0].totalStaff", is(repairStationEquipmentStaff.getTotalStaff())))
//               .andExpect(jsonPath("$.equipmentStaff[0].availableStaff",
//                                   is(repairStationEquipmentStaff.getAvailableStaff())));
//
//    }
//
//    @Test
//    public void testAddRepairStation() throws Exception {
//        RepairStationDTO repairStationDTO = new RepairStationDTO(null,
//                                                                 "repair-station-name",
//                                                                 new RepairStationTypeDTO(2L),
//                                                                 BaseDTO.from(base(3L, "")),
//                                                                 15);
//        when(repairStationService.add(repairStationDTO.getName(),
//                                      repairStationDTO.getBase().getId(),
//                                      repairStationDTO.getType().getId(),
//                                      repairStationDTO.getAmount())).thenReturn(1L);
//
//
//        mockMvc.perform(
//                post("/repair-station").contentType(MediaType.APPLICATION_JSON)
//                                       .content(objectMapper.writeValueAsString(repairStationDTO)))
//               .andExpect(status().isCreated());
//    }
//
//    @Test
//    public void testUpdateRepairStation() throws Exception {
//        Long repairStationId = 3L;
//        RepairStationDTO repairStationDTO = new RepairStationDTO(repairStationId,
//                                                                 "repair-station-name",
//                                                                 new RepairStationTypeDTO(2L),
//                                                                 BaseDTO.from(base(3L, "")),
//                                                                 15);
//
//        mockMvc.perform(
//                put("/repair-station/{id}", repairStationId).contentType(MediaType.APPLICATION_JSON)
//                                                            .content(objectMapper.writeValueAsString(repairStationDTO)))
//               .andExpect(status().isAccepted());
//
//        verify(repairStationService).update(repairStationId,
//                                            repairStationDTO.getName(),
//                                            repairStationDTO.getBase().getId(),
//                                            repairStationDTO.getType().getId(),
//                                            repairStationDTO.getAmount());
//    }
//
//    @Test
//    public void testSetRepairStationEquipmentStaff() throws Exception {
//        EquipmentStaffDTO equipmentStaffDTO = new EquipmentStaffDTO(10, 5);
//        Long repairStationId = 2L;
//        Long equipmentId = 4L;
//
//        mockMvc
//                .perform(post("/repair-station/{repairStationId}/equipment/{equipmentId}", repairStationId, equipmentId)
//                                 .contentType(MediaType.APPLICATION_JSON)
//                                 .content(objectMapper.writeValueAsString(equipmentStaffDTO)))
//                .andExpect(status().isAccepted());
//
//        verify(repairStationService).setEquipmentStaff(repairStationId,
//                                                       equipmentId,
//                                                       equipmentStaffDTO.getAvailableStaff(),
//                                                       equipmentStaffDTO.getTotalStaff());
//    }
//
//    @Test
//    public void testUpdateRepairStationEquipmentStaff() throws Exception {
//        EquipmentStaffDTO equipmentStaffDTO = new EquipmentStaffDTO(10, 5);
//        Long repairStationId = 2L;
//        Long equipmentId = 4L;
//
//        mockMvc
//                .perform(put("/repair-station/{repairStationId}/equipment/{equipmentId}", repairStationId, equipmentId)
//                                 .contentType(MediaType.APPLICATION_JSON)
//                                 .content(objectMapper.writeValueAsString(equipmentStaffDTO)))
//                .andExpect(status().isAccepted());
//
//        verify(repairStationService).updateEquipmentStaff(repairStationId,
//                                                          equipmentId,
//                                                          equipmentStaffDTO.getAvailableStaff(),
//                                                          equipmentStaffDTO.getTotalStaff());
//    }
//
//}
