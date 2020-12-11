package va.rit.teho.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.controller.repairformation.RepairFormationTypeController;
import va.rit.teho.dto.repairformation.RepairFormationTypeDTO;
import va.rit.teho.entity.repairformation.RepairStationType;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RepairFormationTypeController.class)
@ContextConfiguration(classes = TestRunner.class)
public class RepairFormationTypeControllerTest extends ControllerTest {

    @Test
    public void testListRepairStationTypes() throws Exception {
        RepairStationType repairStationType = new RepairStationType("stationType");
        List<RepairStationType> repairStationTypes = Collections.singletonList(repairStationType);
        when(repairFormationTypeService.listTypes()).thenReturn(null);

        mockMvc.perform(get("/repair-station/type"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()", is(repairStationTypes.size())));
    }

    @Test
    public void testAddRepairStationType() throws Exception {
        RepairFormationTypeDTO repairFormationTypeDTO = new RepairFormationTypeDTO(null, "name", 2, 22);

        when(repairFormationTypeService
                     .addType(repairFormationTypeDTO.getName(),
                              repairFormationTypeDTO.getWorkingHoursMin(),
                              repairFormationTypeDTO.getWorkingHoursMax())).thenReturn(2L);

        mockMvc.perform(post("/repair-station/type").contentType(MediaType.APPLICATION_JSON)
                                                    .content(objectMapper.writeValueAsString(repairFormationTypeDTO)))
               .andExpect(status().isCreated());
        verify(repairFormationTypeService).addType(repairFormationTypeDTO.getName(),
                                                   repairFormationTypeDTO.getWorkingHoursMin(),
                                                   repairFormationTypeDTO.getWorkingHoursMax());
    }

    @Test
    public void testUpdateRepairStationType() throws Exception {
        RepairFormationTypeDTO repairFormationTypeDTO = new RepairFormationTypeDTO(2L, "name", 2, 22);


        mockMvc.perform(put("/repair-station/type/{id}",
                            repairFormationTypeDTO.getId().intValue()).contentType(MediaType.APPLICATION_JSON)
                                                                      .content(objectMapper.writeValueAsString(
                                                                              repairFormationTypeDTO)))
               .andExpect(status().isAccepted());
        verify(repairFormationTypeService).updateType(repairFormationTypeDTO.getId(),
                                                      repairFormationTypeDTO.getName(),
                                                      repairFormationTypeDTO.getWorkingHoursMin(),
                                                      repairFormationTypeDTO.getWorkingHoursMax());
    }
}
