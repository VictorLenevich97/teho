package va.rit.teho.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.TestRunner;
import va.rit.teho.entity.common.RepairType;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RepairTypeController.class)
@ContextConfiguration(classes = TestRunner.class)
public class RepairTypeControllerTest extends ControllerTest {

    @Test
    public void testListRepairTypes() throws Exception {
        RepairType repairType = new RepairType("repair-type", "", true, false, false);

        List<RepairType> repairTypeList = Collections.singletonList(repairType);
        when(repairTypeService.list()).thenReturn(repairTypeList);

        mockMvc.perform(get("/repair-type"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()", is(repairTypeList.size())));
    }

    @Test
    public void testListRepairableRepairTypes() throws Exception {
        RepairType repairType = new RepairType("repair-type", "", true, false, false);

        List<RepairType> repairTypeList = Collections.singletonList(repairType);
        when(repairTypeService.list(true)).thenReturn(repairTypeList);

        mockMvc.perform(get("/repair-type?calculatable=true"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()", is(repairTypeList.size())));
    }

}
