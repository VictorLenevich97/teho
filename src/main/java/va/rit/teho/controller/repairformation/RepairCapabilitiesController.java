package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.controller.helper.Formatter;
import va.rit.teho.controller.helper.ReportResponseEntity;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.RowData;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapabilityCombinedData;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.repairformation.RepairCapabilitiesService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;
import va.rit.teho.service.report.ReportService;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "formation/repair-formation/unit", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Производственные возможности РВО")
public class RepairCapabilitiesController {

    private final RepairCapabilitiesService repairCapabilitiesService;
    private final EquipmentService equipmentService;
    private final RepairFormationUnitService repairFormationUnitService;
    private final ReportService<RepairFormationUnitRepairCapabilityCombinedData> reportService;
    @Resource
    private TehoSessionData tehoSession;

    public RepairCapabilitiesController(
            RepairCapabilitiesService repairCapabilitiesService,
            EquipmentService equipmentService,
            RepairFormationUnitService repairFormationUnitService,
            ReportService<RepairFormationUnitRepairCapabilityCombinedData> reportService) {
        this.repairCapabilitiesService = repairCapabilitiesService;
        this.equipmentService = equipmentService;
        this.repairFormationUnitService = repairFormationUnitService;
        this.reportService = reportService;
    }

    /**
     * Расчет производственных возможностей РВО по ремонту (сразу для всех РВО по всем ВВСТ).
     */
    @PostMapping(path = "/capabilities/repair-type/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Расчет производственных возможностей РВО по ремонту (для всех РВО по всем ВВСТ)")
    public ResponseEntity<TableDataDTO<Map<String, String>>> calculateAndGet(@ApiParam(value = "Ключ типа ремонта, по которому производится расчет", required = true) @PathVariable("id") Long repairTypeId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilities(tehoSession.getSessionId(),
                                                                            repairTypeId);
        return getCalculatedRepairCapabilities(repairTypeId,
                                               Collections.emptyList(),
                                               Collections.emptyList(),
                                               Collections.emptyList(),
                                               Collections.emptyList(),
                                               1,
                                               100);
    }

    @PostMapping(path = "/{repairFormationUnitId}/capabilities/repair-type/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Расчет производственных возможностей РВО по ремонту (для указанного РВО)")
    public ResponseEntity<TableDataDTO<Map<String, String>>> calculateAndGetPerStation(@ApiParam(value = "Ключ типа ремонта, по которому производится расчет", required = true) @PathVariable("id") Long repairTypeId,
                                                                                       @ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairFormationUnitId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilitiesPerStation(tehoSession.getSessionId(),
                                                                                      repairFormationUnitId,
                                                                                      repairTypeId);
        TableDataDTO<Map<String, String>> repairCapabilitiesDTO =
                getCalculatedRepairCapabilities(repairTypeId,
                                                Collections.singletonList(repairFormationUnitId),
                                                Collections.emptyList(),
                                                Collections.emptyList(),
                                                Collections.emptyList(), 1,
                                                100).getBody();
        return ResponseEntity.accepted().body(repairCapabilitiesDTO);
    }

    @PutMapping(path = "/{repairFormationUnitId}/capabilities/repair-type/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ручное обновление производственных возможностей РВО по ремонту (для указанного РВО)")
    public ResponseEntity<Object> updateRepairCapabilities(@ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairFormationUnitId,
                                                           @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable("id") Long repairTypeId,
                                                           @ApiParam(value = "Данные в виде {'ключ ВВСТ': 'произв. возможности (ед./сут.)'}", required = true, example = "{'1': '2.15', '2': '3.14'}") @RequestBody Map<Long, Double> data) {
        repairCapabilitiesService.updateRepairCapabilities(tehoSession.getSessionId(),
                                                           repairFormationUnitId,
                                                           repairTypeId,
                                                           data);
        return ResponseEntity.accepted().build();
    }

    private NestedColumnsDTO getRepairCapabilitiesNestedColumnsDTO(Map.Entry<EquipmentType, Map<EquipmentSubType, List<Equipment>>> equipmentTypeEntry) {
        return new NestedColumnsDTO(
                equipmentTypeEntry.getKey().getShortName(),
                equipmentTypeEntry.getValue()
                                  .entrySet()
                                  .stream()
                                  .map(subTypeListEntry ->
                                               new NestedColumnsDTO(subTypeListEntry.getKey().getShortName(),
                                                                    subTypeListEntry.getValue()
                                                                                    .stream()
                                                                                    .map(e -> new NestedColumnsDTO(e
                                                                                                                           .getId()
                                                                                                                           .toString(),
                                                                                                                   e.getName()))
                                                                                    .collect(Collectors.toList())))
                                  .collect(Collectors.toList()));
    }

    private TableDataDTO<Map<String, String>> buildRepairCapabilitiesDTO(RepairFormationUnitRepairCapabilityCombinedData combinedData) {
        List<Equipment> columns =
                combinedData.getGroupedEquipmentData()
                            .values()
                            .stream()
                            .flatMap(l -> l.values().stream())
                            .flatMap(List::stream)
                            .collect(Collectors.toList());
        List<NestedColumnsDTO> equipmentPerTypeDTOList =
                combinedData.getGroupedEquipmentData()
                            .entrySet()
                            .stream()
                            .map(this::getRepairCapabilitiesNestedColumnsDTO)
                            .collect(Collectors.toList());
        List<RowData<Map<String, String>>> data =
                combinedData.getRepairFormationUnitList()
                            .stream()
                            .map(rs -> getRepairCapabilitiesRow(combinedData.getCalculatedRepairCapabilities(),
                                                                columns,
                                                                rs))
                            .collect(Collectors.toList());
        return new TableDataDTO<>(equipmentPerTypeDTOList, data);
    }

    private RowData<Map<String, String>> getRepairCapabilitiesRow(
            Map<RepairFormationUnit, Map<Equipment, Double>> calculatedRepairCapabilities,
            List<Equipment> columns,
            RepairFormationUnit rs) {
        return new RowData<>(
                rs.getId(),
                rs.getName(),
                columns.stream().collect(Collectors.toMap(equipment -> equipment.getId().toString(),
                                                          equipment ->
                                                                  Formatter.formatDouble(calculatedRepairCapabilities
                                                                                                 .getOrDefault(rs,
                                                                                                               Collections
                                                                                                                       .emptyMap())
                                                                                                 .getOrDefault(equipment,
                                                                                                               0.0)))));
    }

    @GetMapping("/capabilities/repair-type/{id}")
    @ResponseBody
    @ApiOperation(value = "Получение расчитанных производственных возможностей РВО по ремонту ВВСТ")
    public ResponseEntity<TableDataDTO<Map<String, String>>> getCalculatedRepairCapabilities(
            @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable("id") Long repairTypeId,
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> repairFormationUnitId,
            @ApiParam(value = "Ключи ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @ApiParam(value = "Ключи подтипов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentSubTypeId,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        RepairFormationUnitRepairCapabilityCombinedData combinedData = getCapabilityCombinedData(
                repairTypeId,
                repairFormationUnitId,
                equipmentId,
                equipmentTypeId,
                equipmentSubTypeId,
                pageNum,
                pageSize);
        TableDataDTO<Map<String, String>> repairCapabilitiesFullDTO = buildRepairCapabilitiesDTO(combinedData);
        return ResponseEntity.ok(repairCapabilitiesFullDTO);
    }

    @GetMapping("/capabilities/repair-type/{id}/report")
    @ResponseBody
    @ApiOperation(value = "Получение расчитанных производственных возможностей РВО по ремонту ВВСТ")
    public ResponseEntity<byte[]> getCalculatedRepairCapabilitiesReport(
            @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable("id") Long repairTypeId,
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> repairFormationUnitId,
            @ApiParam(value = "Ключи ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @ApiParam(value = "Ключи подтипов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentSubTypeId,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) throws UnsupportedEncodingException {
        RepairFormationUnitRepairCapabilityCombinedData combinedData = getCapabilityCombinedData(
                repairTypeId,
                repairFormationUnitId,
                equipmentId,
                equipmentTypeId,
                equipmentSubTypeId,
                pageNum,
                pageSize);

        return ReportResponseEntity.ok("Производственные возможности", reportService.generateReport(combinedData));
    }

    private RepairFormationUnitRepairCapabilityCombinedData getCapabilityCombinedData(Long repairTypeId,
                                                                                      List<Long> repairFormationUnitId,
                                                                                      List<Long> equipmentId,
                                                                                      List<Long> equipmentTypeId,
                                                                                      List<Long> equipmentSubTypeId,
                                                                                      int pageNum,
                                                                                      int pageSize) {
        List<RepairFormationUnit> repairFormationUnitList = repairFormationUnitService.list(repairFormationUnitId,
                                                                                            pageNum,
                                                                                            pageSize);
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> grouped =
                equipmentService.listGroupedByTypes(equipmentId,
                                                    equipmentSubTypeId,
                                                    equipmentTypeId);
        Map<RepairFormationUnit, Map<Equipment, Double>> calculatedRepairCapabilities =
                repairCapabilitiesService.getCalculatedRepairCapabilities(tehoSession.getSessionId(),
                                                                          repairTypeId,
                                                                          repairFormationUnitId,
                                                                          equipmentId,
                                                                          equipmentSubTypeId,
                                                                          equipmentTypeId);
        return new RepairFormationUnitRepairCapabilityCombinedData(
                repairFormationUnitList,
                grouped,
                calculatedRepairCapabilities);
    }

}
