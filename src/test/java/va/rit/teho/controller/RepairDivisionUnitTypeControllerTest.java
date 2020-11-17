package va.rit.teho.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.controller.repairdivision.RepairDivisionUnitTypeController;
import va.rit.teho.dto.repairdivision.RepairDivisionUnitTypeDTO;
import va.rit.teho.entity.repairdivision.RepairStationType;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RepairDivisionUnitTypeController.class)
@ContextConfiguration(classes = TestRunner.class)
public class RepairDivisionUnitTypeControllerTest extends ControllerTest {

    @Test
    public void testListRepairStationTypes() throws Exception {
        RepairStationType repairStationType = new RepairStationType("stationType");
        List<RepairStationType> repairStationTypes = Collections.singletonList(repairStationType);
        when(repairDivisionUnitTypeService.listTypes()).thenReturn(null);

        mockMvc.perform(get("/repair-station/type"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()", is(repairStationTypes.size())));
    }

    @Test
    public void testAddRepairStationType() throws Exception {
        RepairDivisionUnitTypeDTO repairDivisionUnitTypeDTO = new RepairDivisionUnitTypeDTO(null, "name", 2, 22);

        when(repairDivisionUnitTypeService
                     .addType(repairDivisionUnitTypeDTO.getName(),
                              repairDivisionUnitTypeDTO.getWorkingHoursMin(),
                              repairDivisionUnitTypeDTO.getWorkingHoursMax())).thenReturn(2L);

        mockMvc.perform(post("/repair-station/type").contentType(MediaType.APPLICATION_JSON)
                                                    .content(objectMapper.writeValueAsString(repairDivisionUnitTypeDTO)))
               .andExpect(status().isCreated());
        verify(repairDivisionUnitTypeService).addType(repairDivisionUnitTypeDTO.getName(),
                                                      repairDivisionUnitTypeDTO.getWorkingHoursMin(),
                                                      repairDivisionUnitTypeDTO.getWorkingHoursMax());
    }

    @Test
    public void testUpdateRepairStationType() throws Exception {
        RepairDivisionUnitTypeDTO repairDivisionUnitTypeDTO = new RepairDivisionUnitTypeDTO(2L, "name", 2, 22);


        mockMvc.perform(put("/repair-station/type/{id}",
                            repairDivisionUnitTypeDTO.getId().intValue()).contentType(MediaType.APPLICATION_JSON)
                                                                         .content(objectMapper.writeValueAsString(
                                                                                 repairDivisionUnitTypeDTO)))
               .andExpect(status().isAccepted());
        verify(repairDivisionUnitTypeService).updateType(repairDivisionUnitTypeDTO.getId(),
                                                         repairDivisionUnitTypeDTO.getName(),
                                                         repairDivisionUnitTypeDTO.getWorkingHoursMin(),
                                                         repairDivisionUnitTypeDTO.getWorkingHoursMax());
    }
}
