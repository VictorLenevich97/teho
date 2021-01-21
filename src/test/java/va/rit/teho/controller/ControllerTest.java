package va.rit.teho.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairStationType;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.common.StageService;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.implementation.common.RepairTypeServiceImpl;
import va.rit.teho.service.labordistribution.EquipmentRFUDistributionService;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;
import va.rit.teho.service.labordistribution.RestorationTypeService;
import va.rit.teho.service.repairformation.*;
import va.rit.teho.service.session.SessionService;

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

    @MockBean
    protected EquipmentPerFormationService equipmentPerFormationService;

    @MockBean
    protected SessionService sessionService;

    @MockBean
    protected TehoSessionData tehoSessionData;

    @MockBean
    protected RestorationTypeService restorationTypeService;

    @MockBean
    protected StageService stageService;

    @MockBean
    protected EquipmentRFUDistributionService equipmentRFUDistributionService;

    @MockBean
    protected RepairFormationService repairFormationService;

    @MockBean
    protected RepairStationService repairStationService;

    protected Formation base(Long id, String name) {
        return new Formation(id, "short" + name, "full" + name);
    }

    protected Equipment equipment(Long id, String name) {
        return new Equipment(id, name, null);
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
        return new Equipment(id, name, equipmentSubType);
    }

    protected RepairStationType repairStationType(Long id, String name) {
        RepairStationType rst = new RepairStationType(name);
        rst.setId(id);
        return rst;
    }

    protected RepairFormationUnit repairStation(Long id,
                                                String name,
                                                Long typeId,
                                                String typeName) {
        return new RepairFormationUnit(id,
                                       name,
                                       repairStationType(typeId, typeName),
                                       0,
                                       null);
    }
}
