package va.rit.teho.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairStationType;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.implementation.common.RepairTypeServiceImpl;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;
import va.rit.teho.service.repairformation.RepairCapabilitiesService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;
import va.rit.teho.service.repairformation.RepairFormationTypeService;

public abstract class ControllerTest {

    protected ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected FormationService formationService;

    @MockBean
    protected LaborInputDistributionService laborInputDistributionService;

    @MockBean
    protected RepairCapabilitiesService repairCapabilitiesService;

    @MockBean
    protected RepairFormationUnitService repairFormationUnitService;

    @MockBean
    protected RepairFormationTypeService repairFormationTypeService;

    @MockBean
    protected RepairTypeServiceImpl repairTypeService;

    @MockBean
    protected EquipmentService equipmentService;

    @MockBean
    protected EquipmentTypeService equipmentTypeService;

    protected Formation base(Long id, String name) {
        Formation b = new Formation("short" + name, "full" + name);
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
        RepairStationType rst = new RepairStationType(name);
        rst.setId(id);
        return rst;
    }

    protected RepairFormationUnit repairStation(Long id,
                                                String name,
                                                Long typeId,
                                                String typeName,
                                                int workingHoursMin,
                                                int workingHoursMax) {
        RepairFormationUnit repairFormationUnit = new RepairFormationUnit(name,
                                                                          repairStationType(typeId,
                                                                          typeName,
                                                                          workingHoursMin,
                                                                          workingHoursMax),
                                                                          0,
                                                                          null);
        repairFormationUnit.setId(id);
        return repairFormationUnit;
    }
}
