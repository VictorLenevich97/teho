package va.rit.teho.controller.equipment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.controller.ControllerTest;
import va.rit.teho.dto.equipment.EquipmentSubTypeDTO;
import va.rit.teho.dto.equipment.EquipmentTypeDTO;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.model.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EquipmentTypeController.class)
@ContextConfiguration(classes = TestRunner.class)
public class EquipmentTypeControllerTest extends ControllerTest {

    @Test
    public void testGetEquipmentTypes() throws Exception {
        EquipmentType eqType = new EquipmentType("s", "f");
        when(equipmentService.listTypesWithSubTypes()).thenReturn(
                Collections.singletonMap(eqType, Collections.singletonList(new EquipmentSubType("s", "f", eqType))));

        mockMvc.perform(get("/equipment-type")).andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    public void testGetEquipmentById() throws Exception {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        equipmentType.setId(1L);
        EquipmentSubType firstEquipmentSubType = new EquipmentSubType("stShort", "stFull", equipmentType);
        EquipmentSubType secondEquipmentSubType = new EquipmentSubType("secondStShort", "secondStFull", equipmentType);
        List<EquipmentSubType> equipmentSubTypes = Arrays.asList(firstEquipmentSubType, secondEquipmentSubType);

        when(equipmentService.getTypeWithSubTypes(equipmentType.getId())).thenReturn(Pair.of(equipmentType,
                                                                                             equipmentSubTypes));

        mockMvc.perform(get("/equipment-type/{id}", equipmentType.getId())).andExpect(status().isOk())
               .andExpect(jsonPath("$.type.shortName", is(equipmentType.getShortName())))
               .andExpect(jsonPath("$.type.fullName", is(equipmentType.getFullName())))
               .andExpect(jsonPath("$.subTypes.size()", is(equipmentSubTypes.size())));
    }

    @Test
    public void testAddEquipmentType() throws Exception {
        EquipmentTypeDTO equipmentTypeDTO = new EquipmentTypeDTO("shortETName", "fullETName");

        mockMvc.perform(post("/equipment-type").contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(equipmentTypeDTO)))
               .andExpect(status().isCreated());

        verify(equipmentService).addType(equipmentTypeDTO.getShortName(), equipmentTypeDTO.getFullName());
    }

    @Test
    public void testAddEquipmentSubType() throws Exception {
        Long equipmentTypeId = 3L;
        EquipmentSubTypeDTO equipmentSubTypeDTO = new EquipmentSubTypeDTO("subTypeShortName", "subTypeFullName");

        mockMvc.perform(post("/equipment-type/{id}/subtype", equipmentTypeId).contentType(MediaType.APPLICATION_JSON)
                                                                             .content(objectMapper.writeValueAsString(
                                                                                     equipmentSubTypeDTO)))
               .andExpect(status().isCreated());

        verify(equipmentService).addSubType(equipmentTypeId,
                                            equipmentSubTypeDTO.getShortName(),
                                            equipmentSubTypeDTO.getFullName());
    }

}
