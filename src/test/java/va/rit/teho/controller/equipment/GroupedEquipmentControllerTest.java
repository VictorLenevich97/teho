package va.rit.teho.controller.equipment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.controller.ControllerTest;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GroupedEquipmentController.class)
@ContextConfiguration(classes = TestRunner.class)
public class GroupedEquipmentControllerTest extends ControllerTest {

    @Test
    public void testGetEquipmentPerType() throws Exception {
        EquipmentType equipmentType = new EquipmentType("short-name", "full-name");
        EquipmentSubType firstEquipmentSubType = new EquipmentSubType("s1", "f1", equipmentType);
        EquipmentSubType secondEquipmentSubType = new EquipmentSubType("s2", "f2", equipmentType);
        Equipment e1 = new Equipment("eq-name", firstEquipmentSubType);
        Equipment e2 = new Equipment("eq-name-2", firstEquipmentSubType);
        Equipment e3 = new Equipment("eq-name-3", secondEquipmentSubType);
        Map<EquipmentSubType, List<Equipment>> equipmentSubTypeListMap = new HashMap<>();
        equipmentSubTypeListMap.put(firstEquipmentSubType, Arrays.asList(e1, e2));
        equipmentSubTypeListMap.put(secondEquipmentSubType, Collections.singletonList(e3));
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> equipmentTypeMap =
                Collections.singletonMap(equipmentType, equipmentSubTypeListMap);

        when(equipmentService.listGroupedByTypes()).thenReturn(equipmentTypeMap);

        mockMvc.perform(get("/grouped-equipment")).andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(1)))
               .andExpect(jsonPath("$[0].subTypes.size()", is(equipmentSubTypeListMap.size())))
               .andExpect(jsonPath("$[0].subTypes[0].equipment.size()",
                                   is(equipmentSubTypeListMap.get(firstEquipmentSubType).size())))
               .andExpect(jsonPath("$[0].subTypes[0].shortName", is(firstEquipmentSubType.getShortName())))
               .andExpect(jsonPath("$[0].subTypes[0].fullName", is(firstEquipmentSubType.getFullName())))
               .andExpect(jsonPath("$[0].subTypes[1].shortName", is(secondEquipmentSubType.getShortName())))
               .andExpect(jsonPath("$[0].subTypes[1].fullName", is(secondEquipmentSubType.getFullName())))
               .andExpect(jsonPath("$[0].subTypes[1].equipment.size()",
                                   is(equipmentSubTypeListMap.get(secondEquipmentSubType).size())));
    }

}
