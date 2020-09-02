package va.rit.teho.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import va.rit.teho.service.*;

public abstract class ControllerTest {

    protected ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    protected BaseService baseService;

    @MockBean
    protected LaborInputDistributionService laborInputDistributionService;

    @MockBean
    protected RepairCapabilitiesService repairCapabilitiesService;

    @MockBean
    protected RepairStationService repairStationService;

    @MockBean
    protected EquipmentService equipmentService;
}
