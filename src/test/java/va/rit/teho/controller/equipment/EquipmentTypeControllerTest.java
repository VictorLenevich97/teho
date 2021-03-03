package va.rit.teho.controller.equipment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.controller.ControllerTest;
import va.rit.teho.dto.equipment.EquipmentTypeDTO;
import va.rit.teho.entity.equipment.EquipmentType;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EquipmentTypeController.class)
@ContextConfiguration(classes = TestRunner.class)
public class EquipmentTypeControllerTest extends ControllerTest {

    @Test
    public void testGetEquipmentTypes() throws Exception {
        EquipmentType eqType = new EquipmentType("s", "f");
        when(equipmentTypeService.listTypes(null)).thenReturn(Collections.singletonList(eqType));

        mockMvc.perform(get("/equipment-type")).andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    public void testGetEquipmentTypeById() throws Exception {
        EquipmentType equipmentType = new EquipmentType("stShort", "stFull");
        equipmentType.setId(1L);
        equipmentType.setEquipmentTypes(Collections.emptySet());

        when(equipmentTypeService.get(equipmentType.getId())).thenReturn(equipmentType);

        mockMvc.perform(get("/equipment-type/{id}", equipmentType.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.shortName", is(equipmentType.getShortName())))
                .andExpect(jsonPath("$.fullName", is(equipmentType.getFullName())));
    }

    @Test
    public void testAddEquipmentType() throws Exception {
        EquipmentTypeDTO equipmentTypeDTO = new EquipmentTypeDTO("shortETName", "fullETName", Collections.emptyList());

        when(equipmentTypeService.addType(equipmentTypeDTO.getShortName(),
                                          equipmentTypeDTO.getFullName())).thenReturn(new EquipmentType(
                equipmentTypeDTO.getShortName(),
                equipmentTypeDTO.getFullName()));

        mockMvc.perform(post("/equipment-type").contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(equipmentTypeDTO)))
               .andExpect(status().isCreated());

        verify(equipmentTypeService).addType(equipmentTypeDTO.getShortName(), equipmentTypeDTO.getFullName());
    }

    @Test
    public void testUpdateEquipmentType() throws Exception {
        Long equipmentId = 3L;
        EquipmentTypeDTO equipmentTypeDTO = new EquipmentTypeDTO(equipmentId, "shortETName", "fullETName");

        when(equipmentTypeService.updateType(equipmentId,
                                             equipmentTypeDTO.getShortName(),
                                             equipmentTypeDTO.getFullName())).thenReturn(new EquipmentType(
                equipmentTypeDTO.getShortName(),
                equipmentTypeDTO.getFullName()));

        mockMvc.perform(put("/equipment-type/{id}", equipmentId).contentType(MediaType.APPLICATION_JSON)
                                                                .content(objectMapper.writeValueAsString(
                                                                        equipmentTypeDTO)))
               .andExpect(status().isAccepted());

        verify(equipmentTypeService).updateType(equipmentId,
                                                equipmentTypeDTO.getShortName(),
                                                equipmentTypeDTO.getFullName());
    }

}
