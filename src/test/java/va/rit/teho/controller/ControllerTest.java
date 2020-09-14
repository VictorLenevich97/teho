package va.rit.teho.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import va.rit.teho.entity.*;
import va.rit.teho.service.*;
import va.rit.teho.service.implementation.RepairTypeServiceImpl;

public abstract class ControllerTest {

    protected ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected BaseService baseService;

    @MockBean
    protected LaborInputDistributionService laborInputDistributionService;

    @MockBean
    protected RepairCapabilitiesService repairCapabilitiesService;

    @MockBean
    protected RepairStationService repairStationService;

    @MockBean
    protected RepairStationTypeService repairStationtypeService;

    @MockBean
    protected RepairTypeServiceImpl repairTypeService;

    @MockBean
    protected EquipmentService equipmentService;

    @MockBean
    protected EquipmentTypeService equipmentTypeService;

    protected Base base(Long id, String name) {
        Base b = new Base("short" + name, "full" + name);
        b.setId(id);
        return b;
    }

    protected Equipment equipment(Long id, String name) {
        Equipment e = new Equipment(name, null);
        e.setId(id);
        return e;
    }

    protected Equipment equipment(Long id,
                                  String name,
                                  Long subTypeId,
                                  String subTypeName,
                                  Long typeId,
                                  String typeName) {
        EquipmentType equipmentType = new EquipmentType("short" + typeName, "full" + typeName);
        equipmentType.setId(typeId);
        EquipmentSubType equipmentSubType = new EquipmentSubType("short" + subTypeName,
                                                                 "full" + subTypeName,
                                                                 equipmentType);
        equipmentSubType.setId(subTypeId);
        Equipment e = new Equipment(name, equipmentSubType);
        e.setId(id);
        return e;
    }

    protected RepairStationType repairStationType(Long id, String name, int workingHoursMin, int workingHoursMax) {
        RepairStationType rst = new RepairStationType(name, workingHoursMin, workingHoursMax);
        rst.setId(id);
        return rst;
    }

    protected RepairStation repairStation(Long id,
                                          String name,
                                          Long typeId,
                                          String typeName,
                                          int workingHoursMin,
                                          int workingHoursMax,
                                          Base b) {
        RepairStation repairStation = new RepairStation(name,
                                                        repairStationType(typeId,
                                                                          typeName,
                                                                          workingHoursMin,
                                                                          workingHoursMax),
                                                        b,
                                                        3);
        repairStation.setId(id);
        return repairStation;
    }
}
