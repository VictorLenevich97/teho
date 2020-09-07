package va.rit.teho.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.dto.BaseDTO;
import va.rit.teho.dto.equipment.IntensityAndAmountDTO;
import va.rit.teho.entity.Base;
import va.rit.teho.exception.BaseNotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BaseController.class)
@ContextConfiguration(classes = TestRunner.class)
public class BaseControllerTest extends ControllerTest {

    @Test
    public void testListBases() throws Exception {
        List<Base> bases = Arrays.asList(new Base("s", "f"), new Base("s2", "f2"));
        when(baseService.list()).thenReturn(bases);

        mockMvc.perform(get("/base")).andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(bases.size())));
    }

    @Test
    public void testGetBaseById() throws Exception {
        Long baseId = 1L;
        Base base = new Base("s", "f");
        base.setId(baseId);
        when(baseService.get(baseId)).thenReturn(base);

        mockMvc.perform(get("/base/{id}", baseId))
               .andExpect(status().isOk()).andExpect(jsonPath("$.key", is(baseId.intValue())))
               .andExpect(jsonPath("$.shortName", is(base.getShortName())))
               .andExpect(jsonPath("$.fullName", is(base.getFullName())));
    }

    @Test
    public void testBaseNotFound() throws Exception {
        Long baseId = 1L;
        when(baseService.get(baseId)).thenThrow(new BaseNotFoundException(baseId));

        mockMvc
                .perform(get("/base/{baseId}", baseId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddBase() throws Exception {
        Long baseId = 1L;
        BaseDTO base = new BaseDTO(baseId, "short", "full");
        when(baseService.add(base.getShortName(), base.getFullName())).thenReturn(baseId);

        mockMvc.perform(
                post("/base").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(base)))
               .andExpect(status().isCreated());

        verify(baseService).add(base.getShortName(), base.getFullName());
    }

    @Test
    public void testAddEquipmentToBase() throws Exception {
        Long baseId = 1L;
        Long equipmentId = 2L;
        IntensityAndAmountDTO intensityAndAmountDTO = new IntensityAndAmountDTO(12, 10);

        mockMvc
                .perform(post("/base/{baseId}/equipment/{equipmentId}", baseId, equipmentId)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(intensityAndAmountDTO)))
                .andExpect(status().isAccepted());

        verify(baseService).addEquipmentToBase(baseId,
                                               equipmentId,
                                               intensityAndAmountDTO.getIntensity(),
                                               intensityAndAmountDTO.getAmount());
    }


}
