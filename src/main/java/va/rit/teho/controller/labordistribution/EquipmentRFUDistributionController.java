package va.rit.teho.controller.labordistribution;

import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import va.rit.teho.controller.helper.ReportResponseEntity;
import va.rit.teho.dto.labordistribution.EquipmentRFUDistributionDTO;
import va.rit.teho.entity.labordistribution.EquipmentDistributionCombinedData;
import va.rit.teho.entity.labordistribution.EquipmentPerFormationDistributionData;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.labordistribution.EquipmentRFUDistributionService;
import va.rit.teho.service.labordistribution.RestorationTypeService;
import va.rit.teho.service.report.ReportService;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/formation", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Распределение ремонтного фонда")
public class EquipmentRFUDistributionController {

    private final EquipmentRFUDistributionService equipmentRFUDistributionService;
    private final RepairTypeService repairTypeService;
    private final RestorationTypeService restorationTypeService;

    private final ReportService<EquipmentDistributionCombinedData> distributionReportService;

    @Resource
    private TehoSessionData tehoSession;

    public EquipmentRFUDistributionController(EquipmentRFUDistributionService equipmentRFUDistributionService,
                                              RepairTypeService repairTypeService,
                                              RestorationTypeService restorationTypeService,
                                              ReportService<EquipmentDistributionCombinedData> distributionReportService) {
        this.equipmentRFUDistributionService = equipmentRFUDistributionService;
        this.repairTypeService = repairTypeService;
        this.restorationTypeService = restorationTypeService;
        this.distributionReportService = distributionReportService;
    }

    @PostMapping("/repair-formation/unit/equipment")
    public ResponseEntity<Object> distributeEquipmentPerRFU() {
        equipmentRFUDistributionService.distribute(tehoSession.getSessionId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/repair-formation/unit/{repairFormationUnitId}/equipment")
    public ResponseEntity<List<EquipmentRFUDistributionDTO>> getDistributedEquipmentForRFU(@PathVariable Long repairFormationUnitId) {
        List<EquipmentRFUDistributionDTO> equipmentRFUDistributionDTOList = equipmentRFUDistributionService
                .listRFUDistributedEquipment(repairFormationUnitId, tehoSession.getSessionId())
                .stream()
                .map(EquipmentRFUDistributionDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(equipmentRFUDistributionDTOList);
    }

    @GetMapping("/{formationId}/distribution/report")
    public ResponseEntity<byte[]> getEquipmentDistributionReport(@PathVariable Long formationId) throws
            UnsupportedEncodingException {
        List<EquipmentPerFormationDistributionData> equipmentPerFormationDistributionData =
                equipmentRFUDistributionService.listDistributionDataForFormation(tehoSession.getSessionId(),
                                                                                 formationId);
        byte[] bytes = distributionReportService.generateReport(
                new EquipmentDistributionCombinedData(repairTypeService.list(true),
                                                      restorationTypeService.list(),
                                                      equipmentPerFormationDistributionData));

        return ReportResponseEntity.ok("Результаты решения задачи", bytes);
    }


}
