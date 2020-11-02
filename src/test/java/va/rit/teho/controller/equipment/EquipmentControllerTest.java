package va.rit.teho.controller.equipment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.controller.ControllerTest;
import va.rit.teho.dto.equipment.EquipmentDTO;
import va.rit.teho.dto.equipment.EquipmentSubTypeDTO;
import va.rit.teho.entity.equipment.Equipment;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EquipmentController.class)
@ContextConfiguration(classes = TestRunner.class)
public class EquipmentControllerTest extends ControllerTest {

    @Test
    public void testGetEquipmentList() throws Exception {
        Equipment equipment = equipment(1L, "eqName");

        when(equipmentService.list()).thenReturn(Collections.singletonList(equipment));

        mockMvc.perform(get("/equipment"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()", is(1)))
               .andExpect(jsonPath("$[0].id", is(equipment.getId().intValue())))
               .andExpect(jsonPath("$[0].name", is(equipment.getName())));
    }

    @Test
    public void testGetEquipmentById() throws Exception {
        Equipment equipment = equipment(1L, "equipName", 2L, "subTypeName", 3L, "typeName");

        when(equipmentService.get(equipment.getId())).thenReturn(equipment);

        mockMvc.perform(get("/equipment/{id}", equipment.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(equipment.getId().intValue())))
               .andExpect(jsonPath("$.name", is(equipment.getName())))
               .andExpect(jsonPath("$.type.shortName",
                                   is(equipment.getEquipmentSubType().getEquipmentType().getShortName())))
               .andExpect(jsonPath("$.type.fullName",
                                   is(equipment.getEquipmentSubType().getEquipmentType().getFullName())))
               .andExpect(jsonPath("$.subType.shortName", is(equipment.getEquipmentSubType().getShortName())))
               .andExpect(jsonPath("$.subType.fullName", is(equipment.getEquipmentSubType().getFullName())));
    }

    @Test
    public void testAddNewEquipment() throws Exception {
        EquipmentDTO equipmentDTO = new EquipmentDTO("equipmentName");
        equipmentDTO.setId(3L);
        equipmentDTO.setSubType(new EquipmentSubTypeDTO(2L, "", ""));

        mockMvc.perform(post("/equipment").contentType(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(equipmentDTO)))
               .andExpect(status().isCreated());

        verify(equipmentService).add(equipmentDTO.getName(), equipmentDTO.getSubType().getId());
    }

    @Test
    public void testUpdateEquipment() throws Exception {
        EquipmentDTO equipmentDTO = new EquipmentDTO("equipmentName");
        equipmentDTO.setId(3L);
        equipmentDTO.setSubType(new EquipmentSubTypeDTO(2L, "", ""));

        mockMvc.perform(put("/equipment/{id}", equipmentDTO.getId()).contentType(MediaType.APPLICATION_JSON)
                                                                    .content(objectMapper.writeValueAsString(
                                                                            equipmentDTO)))
               .andExpect(status().isAccepted());

        verify(equipmentService).update(equipmentDTO.getId(),
                                        equipmentDTO.getName(),
                                        equipmentDTO.getSubType().getId());
    }
}
