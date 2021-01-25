package va.rit.teho.controller.labordistribution;

import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.controller.helper.ReportResponseEntity;
import va.rit.teho.dto.labordistribution.EquipmentDistributionRowData;
import va.rit.teho.dto.labordistribution.EquipmentDistributionTableDataDTO;
import va.rit.teho.dto.labordistribution.EquipmentRFUDistributionDTO;
import va.rit.teho.dto.labordistribution.LaborDistributionFilterData;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.labordistribution.EquipmentDistributionCombinedData;
import va.rit.teho.entity.labordistribution.EquipmentPerFormationDistributionData;
import va.rit.teho.entity.labordistribution.RestorationType;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.labordistribution.EquipmentRFUDistributionService;
import va.rit.teho.service.labordistribution.RestorationTypeService;
import va.rit.teho.service.report.ReportService;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private <T> List<T> nullIfEmpty(List<T> data) {
        return data.isEmpty() ? null : data;
    }

    @PostMapping("/repair-formation/unit/equipment")
    public ResponseEntity<Object> distributeEquipmentPerRFU(@RequestBody LaborDistributionFilterData filterData) {
        equipmentRFUDistributionService.distribute(tehoSession.getSessionId(),
                                                   nullIfEmpty(filterData.getEquipmentIds()),
                                                   nullIfEmpty(filterData.getFormationIds()),
                                                   nullIfEmpty(filterData.getRepairFormationUnitIds()));
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


    @GetMapping("/{formationId}/distribution")
    public ResponseEntity<EquipmentDistributionTableDataDTO> getEquipmentDistribution(@PathVariable Long formationId) {
        List<EquipmentPerFormationDistributionData> equipmentPerFormationDistributionData =
                equipmentRFUDistributionService.listDistributionDataForFormation(tehoSession.getSessionId(),
                                                                                 formationId);
        List<RepairType> repairTypes = repairTypeService.list(true);
        List<RestorationType> restorationTypes = restorationTypeService.list();

        List<EquipmentDistributionRowData> equipmentDistributionRowData = equipmentPerFormationDistributionData
                .stream()
                .map(epfdd -> {
                    Map<String, Double> repairTypeAmountMap = new HashMap<>();
                    epfdd
                            .getAmountPerRepairType()
                            .forEach((rt, amount) -> repairTypeAmountMap.put(rt.getId().toString(), amount));
                    Map<String, Double> restorationTypeAmountMap = new HashMap<>();
                    epfdd
                            .getAmountPerRestorationType()
                            .forEach((rt, amount) -> restorationTypeAmountMap.put(rt.getId().toString(), amount));

                    return new EquipmentDistributionRowData(epfdd.getEquipment().getId(),
                                                            epfdd.getEquipment().getName(),
                                                            epfdd.getAmount(),
                                                            epfdd.getAvgDailyFailure(),
                                                            repairTypeAmountMap,
                                                            restorationTypeAmountMap);
                })
                .collect(Collectors.toList());

        EquipmentDistributionTableDataDTO tableDataDTO =
                new EquipmentDistributionTableDataDTO(
                        repairTypes
                                .stream()
                                .map(rt -> new NestedColumnsDTO(rt.getId().toString(), rt.getShortName()))
                                .collect(Collectors.toList()),
                        restorationTypes
                                .stream()
                                .map(rt -> new NestedColumnsDTO(rt.getId().toString(), rt.getName()))
                                .collect(Collectors.toList()),
                        equipmentDistributionRowData);

        return ResponseEntity.ok(tableDataDTO);
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
