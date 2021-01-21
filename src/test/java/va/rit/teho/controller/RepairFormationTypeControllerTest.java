package va.rit.teho.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.controller.repairformation.RepairFormationTypeController;
import va.rit.teho.dto.common.IdAndNameDTO;
import va.rit.teho.dto.repairformation.RepairFormationTypeDTO;
import va.rit.teho.entity.labordistribution.RestorationType;
import va.rit.teho.entity.repairformation.RepairFormationType;

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
    public void testListRepairFormationTypes() throws Exception {
        List<RepairFormationType> repairFormationTypes = Collections.singletonList(new RepairFormationType(null,
                                                                                                           "",
                                                                                                           new RestorationType(
                                                                                                                   1L,
                                                                                                                   "",
                                                                                                                   1),
                                                                                                           0,
                                                                                                           0));
        when(repairFormationTypeService.listTypes()).thenReturn(repairFormationTypes);

        mockMvc.perform(get("/formation/repair-formation/type"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()", is(repairFormationTypes.size())));
    }

    @Test
    public void testAddRepairFormationType() throws Exception {
        RepairFormationTypeDTO repairFormationTypeDTO = new RepairFormationTypeDTO(null,
                                                                                   "name",
                                                                                   new IdAndNameDTO(2L, ""),
                                                                                   2,
                                                                                   22);

        when(repairFormationTypeService
                     .addType(repairFormationTypeDTO.getName(),
                              2L,
                              repairFormationTypeDTO.getWorkingHoursMin(),
                              repairFormationTypeDTO.getWorkingHoursMax())).thenReturn(2L);

        mockMvc.perform(post("/formation/repair-formation/type").contentType(MediaType.APPLICATION_JSON)
                                                                .content(objectMapper.writeValueAsString(
                                                                        repairFormationTypeDTO)))
               .andExpect(status().isCreated());
        verify(repairFormationTypeService).addType(repairFormationTypeDTO.getName(),
                                                   2L,
                                                   repairFormationTypeDTO.getWorkingHoursMin(),
                                                   repairFormationTypeDTO.getWorkingHoursMax());
    }

    @Test
    public void testUpdateRepairFormationType() throws Exception {
        RepairFormationTypeDTO repairFormationTypeDTO = new RepairFormationTypeDTO(2L,
                                                                                   "name",
                                                                                   new IdAndNameDTO(3L, ""),
                                                                                   2,
                                                                                   22);


        mockMvc.perform(put("/formation/repair-formation/type/{id}",
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
