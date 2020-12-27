package va.rit.teho.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.controller.formation.FormationController;
import va.rit.teho.dto.formation.FormationDTO;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.exception.FormationNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FormationController.class)
@ContextConfiguration(classes = TestRunner.class)
public class FormationControllerTest extends ControllerTest {

    @Test
    public void testListBases() throws Exception {
        List<Formation> bases = Arrays.asList(new Formation(1L, "s", "f"), new Formation(1L, "s2", "f2"));
        when(formationService.list()).thenReturn(bases);

        mockMvc.perform(get("/formation")).andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(bases.size())));
    }

    @Test
    public void testGetBaseById() throws Exception {
        Long formationId = 1L;
        Formation formation = new Formation(1L, "s", "f");
        formation.setId(formationId);
        when(formationService.get(formationId)).thenReturn(formation);

        mockMvc.perform(get("/formation/{id}", formationId))
               .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(formationId.intValue())))
               .andExpect(jsonPath("$.shortName", is(formation.getShortName())))
               .andExpect(jsonPath("$.fullName", is(formation.getFullName())));
    }

    @Test
    public void testBaseNotFound() throws Exception {
        Long formationId = 1L;
        when(formationService.get(formationId)).thenThrow(new FormationNotFoundException(formationId));

        mockMvc
                .perform(get("/formation/{formationId}", formationId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddBase() throws Exception {
        Formation formation = new Formation();
        Long formationId = 1L;
        formation.setId(formationId);
        FormationDTO base = new FormationDTO(formationId, "short", "full", null, Collections.emptyList());
        when(formationService.add(base.getShortName(), base.getFullName())).thenReturn(formation);

        mockMvc.perform(
                post("/formation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(base)))
               .andExpect(status().isCreated());

        verify(formationService).add(base.getShortName(), base.getFullName());
    }

    @Test
    public void testUpdateBase() throws Exception {
        Formation formation = new Formation();
        Long formationId = 1L;
        formation.setId(formationId);
        FormationDTO base = new FormationDTO(formationId, "short", "full", null, Collections.emptyList());
        when(formationService.add(base.getShortName(), base.getFullName())).thenReturn(formation);

        mockMvc.perform(
                put("/formation/{id}", formationId).contentType(MediaType.APPLICATION_JSON)
                                                   .content(objectMapper.writeValueAsString(base)))
               .andExpect(status().isAccepted());

        verify(formationService).update(formationId, base.getShortName(), base.getFullName());
    }
//
//    @Test
//    public void testAddEquipmentToBase() throws Exception {
//        Long formationId = 1L;
//        Long equipmentId = 2L;
//        IntensityAndAmountDTO intensityAndAmountDTO = new IntensityAndAmountDTO(Collections.singletonList(new IntensityAndAmountDTO.IntensityPerRepairTypeAndStageDTO(
//                1L,
//                1L,
//                1)), 10);
//
//        mockMvc
//                .perform(post("/formation/{formationId}/equipment/{equipmentId}", formationId, equipmentId)
//                                 .contentType(MediaType.APPLICATION_JSON)
//                                 .content(objectMapper.writeValueAsString(intensityAndAmountDTO)))
//                .andExpect(status().isAccepted());
//
//        verify(baseService).addEquipmentToBase(formationId,
//                                               equipmentId,
//                                               intensityAndAmountDTO.getAmount());
//    }
//
//    @Test
//    public void testUpdateEquipmentToBase() throws Exception {
//        Long formationId = 1L;
//        Long equipmentId = 2L;
//        IntensityAndAmountDTO intensityAndAmountDTO = new IntensityAndAmountDTO(Collections.singletonList(new IntensityAndAmountDTO.IntensityPerRepairTypeAndStageDTO(
//                1L,
//                1L,
//                1)), 10);
//
//        mockMvc
//                .perform(put("/formation/{formationId}/equipment/{equipmentId}", formationId, equipmentId)
//                                 .contentType(MediaType.APPLICATION_JSON)
//                                 .content(objectMapper.writeValueAsString(intensityAndAmountDTO)))
//                .andExpect(status().isAccepted());
//
//        verify(baseService).updateEquipmentInBase(formationId,
//                                                  equipmentId,
//                                                  intensityAndAmountDTO.getAmount());
//    }


}
