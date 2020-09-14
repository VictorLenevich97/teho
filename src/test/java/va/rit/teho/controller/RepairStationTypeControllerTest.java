package va.rit.teho.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.dto.RepairStationTypeDTO;
import va.rit.teho.entity.RepairStationType;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RepairStationTypeController.class)
@ContextConfiguration(classes = TestRunner.class)
public class RepairStationTypeControllerTest extends ControllerTest {

    @Test
    public void testListRepairStationTypes() throws Exception {
        RepairStationType repairStationType = new RepairStationType("stationType", 5, 25);
        List<RepairStationType> repairStationTypes = Collections.singletonList(repairStationType);
        when(repairStationTypeService.listTypes()).thenReturn(repairStationTypes);

        mockMvc.perform(get("/repair-station/type"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()", is(repairStationTypes.size())));
    }

    @Test
    public void testAddRepairStationType() throws Exception {
        RepairStationTypeDTO repairStationTypeDTO = new RepairStationTypeDTO(null, "name", 2, 22);

        when(repairStationTypeService
                     .addType(repairStationTypeDTO.getName(),
                              repairStationTypeDTO.getWorkingHoursMin(),
                              repairStationTypeDTO.getWorkingHoursMax())).thenReturn(2L);

        mockMvc.perform(post("/repair-station/type").contentType(MediaType.APPLICATION_JSON)
                                                    .content(objectMapper.writeValueAsString(repairStationTypeDTO)))
               .andExpect(status().isCreated());
        verify(repairStationTypeService).addType(repairStationTypeDTO.getName(),
                                                 repairStationTypeDTO.getWorkingHoursMin(),
                                                 repairStationTypeDTO.getWorkingHoursMax());
    }

    @Test
    public void testUpdateRepairStationType() throws Exception {
        RepairStationTypeDTO repairStationTypeDTO = new RepairStationTypeDTO(2L, "name", 2, 22);


        mockMvc.perform(put("/repair-station/type/{id}",
                            repairStationTypeDTO.getId().intValue()).contentType(MediaType.APPLICATION_JSON)
                                                                    .content(objectMapper.writeValueAsString(
                                                                            repairStationTypeDTO)))
               .andExpect(status().isAccepted());
        verify(repairStationTypeService).updateType(repairStationTypeDTO.getId(),
                                                    repairStationTypeDTO.getName(),
                                                    repairStationTypeDTO.getWorkingHoursMin(),
                                                    repairStationTypeDTO.getWorkingHoursMax());
    }
}
