package va.rit.teho.controller.equipment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.controller.ControllerTest;
import va.rit.teho.dto.equipment.EquipmentSubTypeDTO;
import va.rit.teho.dto.equipment.EquipmentTypeDTO;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        when(equipmentTypeService.listTypes(Collections.emptyList())).thenReturn(Collections.singletonList(eqType));

        mockMvc.perform(get("/equipment-type")).andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    public void testGetEquipmentById() throws Exception {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        equipmentType.setId(1L);
        EquipmentSubType firstEquipmentSubType = new EquipmentSubType("stShort", "stFull", equipmentType);
        EquipmentSubType secondEquipmentSubType = new EquipmentSubType("secondStShort", "secondStFull", equipmentType);
        List<EquipmentSubType> equipmentSubTypes = Arrays.asList(firstEquipmentSubType, secondEquipmentSubType);

        when(equipmentTypeService.getTypeWithSubTypes(equipmentType.getId())).thenReturn(Pair.of(equipmentType,
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

        verify(equipmentTypeService).addType(equipmentTypeDTO.getShortName(), equipmentTypeDTO.getFullName());
    }

    @Test
    public void testAddEquipmentSubType() throws Exception {
        Long equipmentTypeId = 3L;
        EquipmentSubTypeDTO equipmentSubTypeDTO = new EquipmentSubTypeDTO("subTypeShortName", "subTypeFullName");

        mockMvc.perform(post("/equipment-type/{id}/subtype", equipmentTypeId).contentType(MediaType.APPLICATION_JSON)
                                                                             .content(objectMapper.writeValueAsString(
                                                                                     equipmentSubTypeDTO)))
               .andExpect(status().isCreated());

        verify(equipmentTypeService).addSubType(equipmentTypeId,
                                                equipmentSubTypeDTO.getShortName(),
                                                equipmentSubTypeDTO.getFullName());
    }

    @Test
    public void testGetEquipmentSubTypes() throws Exception {
        List<EquipmentSubType> equipmentSubTypes = Collections.singletonList(new EquipmentSubType(
                "s",
                "f",
                new EquipmentType("s",
                                  "f")));
        when(equipmentTypeService.listSubTypes(null)).thenReturn(equipmentSubTypes);

        mockMvc.perform(get("/equipment-type/subtype"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()", is(equipmentSubTypes.size())))
               .andExpect(jsonPath("$[0].shortName", is(equipmentSubTypes.get(0).getShortName())))
               .andExpect(jsonPath("$[0].fullName", is(equipmentSubTypes.get(0).getFullName())));
    }

    @Test
    public void testGetEquipmentSubTypesWithFilters() throws Exception {

        List<EquipmentSubType> equipmentSubTypes = Collections.singletonList(new EquipmentSubType(
                "s",
                "f",
                new EquipmentType("s",
                                  "f")));
        when(equipmentTypeService.listSubTypes(Arrays.asList(2L, 3L))).thenReturn(equipmentSubTypes);

        mockMvc.perform(get("/equipment-type/subtype?typeId=2,3"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()", is(equipmentSubTypes.size())))
               .andExpect(jsonPath("$[0].shortName", is(equipmentSubTypes.get(0).getShortName())))
               .andExpect(jsonPath("$[0].fullName", is(equipmentSubTypes.get(0).getFullName())));
    }

    @Test
    public void testUpdateEquipmentType() throws Exception {
        Long equipmentId = 3L;
        EquipmentTypeDTO equipmentTypeDTO = new EquipmentTypeDTO("shortETName", "fullETName");

        mockMvc.perform(put("/equipment-type/{id}", equipmentId).contentType(MediaType.APPLICATION_JSON)
                                                                .content(objectMapper.writeValueAsString(
                                                                        equipmentTypeDTO)))
               .andExpect(status().isAccepted());

        verify(equipmentTypeService).updateType(equipmentId,
                                                equipmentTypeDTO.getShortName(),
                                                equipmentTypeDTO.getFullName());
    }

    @Test
    public void testUpdateEquipmentSubType() throws Exception {
        Long equipmentTypeId = 2L;
        Long equipmentSubTypeId = 3L;
        EquipmentSubTypeDTO equipmentSubTypeDTO = new EquipmentSubTypeDTO("subTypeShortName", "subTypeFullName");

        mockMvc.perform(put("/equipment-type/{id}/subtype/{subTypeId}",
                            equipmentTypeId,
                            equipmentSubTypeId).contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(
                                                       equipmentSubTypeDTO)))
               .andExpect(status().isAccepted());

        verify(equipmentTypeService).updateSubType(equipmentSubTypeId,
                                                   equipmentTypeId,
                                                   equipmentSubTypeDTO.getShortName(),
                                                   equipmentSubTypeDTO.getFullName());
    }

}
