package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.controller.helper.Formatter;
import va.rit.teho.controller.helper.ReportResponseEntity;
import va.rit.teho.dto.repairformation.EquipmentStaffPerSubType;
import va.rit.teho.dto.repairformation.EquipmentTypeStaffData;
import va.rit.teho.dto.repairformation.RepairCapabilityPerEquipment;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.RowData;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapability;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapabilityCombinedData;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.repairformation.RepairCapabilitiesService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;
import va.rit.teho.service.report.ReportService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static va.rit.teho.controller.helper.FilterConverter.nullIfEmpty;

@Controller
@Validated
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
    public ResponseEntity<Object> calculateAndGetPerStation(@ApiParam(value = "Ключ типа ремонта, по которому производится расчет", required = true) @PathVariable("id") Long repairTypeId,
                                                            @ApiParam(value = "Ключ РВО", required = true) @PathVariable @Positive Long repairFormationUnitId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilitiesPerStation(tehoSession.getSessionId(),
                                                                                      repairFormationUnitId,
                                                                                      repairTypeId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping(path = "/{repairFormationUnitId}/capabilities/repair-type/{id}/batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ручное обновление производственных возможностей РВО по ремонту (для указанного РВО), сразу для многих ВВСТ")
    public ResponseEntity<Object> updateRepairCapabilitiesBatch(@ApiParam(value = "Ключ РВО", required = true) @PathVariable @Positive Long repairFormationUnitId,
                                                                @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable("id") Long repairTypeId,
                                                                @ApiParam(value = "Данные в виде {'ключ ВВСТ': 'произв. возможности (ед./сут.)'}", required = true, example = "{'1': '2.15', '2': '3.14'}") @RequestBody Map<Long, Double> data) {
        repairCapabilitiesService.updateRepairCapabilities(tehoSession.getSessionId(),
                                                           repairFormationUnitId,
                                                           repairTypeId,
                                                           data);
        return ResponseEntity.accepted().build();
    }

    @PutMapping(path = "/{repairFormationUnitId}/capabilities/repair-type/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ручное обновление производственных возможностей РВО по ремонту (для указанного РВО)")
    public ResponseEntity<Object> updateRepairCapabilities(@ApiParam(value = "Ключ РВО", required = true) @PathVariable @Positive Long repairFormationUnitId,
                                                           @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable("id") Long repairTypeId,
                                                           @ApiParam(value = "Данные по произв. возможностям", required = true) @Valid @RequestBody RepairCapabilityPerEquipment repairCapabilityPerEquipment) {
        RepairFormationUnitRepairCapability repairFormationUnitRepairCapability =
                repairCapabilitiesService.updateRepairCapabilities(
                        tehoSession.getSessionId(),
                        repairFormationUnitId,
                        repairTypeId,
                        repairCapabilityPerEquipment.getId(),
                        repairCapabilityPerEquipment.getCapability());
        return ResponseEntity.accepted().body(
                new RepairCapabilityPerEquipment(repairFormationUnitRepairCapability
                                                         .getEquipmentPerRepairFormationUnitPK()
                                                         .getEquipmentId(),
                                                 repairCapabilityPerEquipment.getName(),
                                                 repairFormationUnitRepairCapability.getCapability()));
    }

    private Stream<NestedColumnsDTO> getRepairCapabilitiesNestedColumnsDTO(Map.Entry<EquipmentType, Map<EquipmentSubType, List<Equipment>>> equipmentTypeEntry) {
        if (equipmentTypeEntry.getKey() == null) {
            return equipmentTypeEntry
                    .getValue()
                    .entrySet()
                    .stream()
                    .map(this::getNestedColumnsDTO);
        } else {
            return Stream.of(new NestedColumnsDTO(
                    equipmentTypeEntry.getKey().getShortName(),
                    equipmentTypeEntry.getValue()
                                      .entrySet()
                                      .stream()
                                      .map(this::getNestedColumnsDTO)
                                      .collect(Collectors.toList())));
        }
    }

    private NestedColumnsDTO getNestedColumnsDTO(Map.Entry<EquipmentSubType, List<Equipment>> subTypeListEntry) {
        return new NestedColumnsDTO(subTypeListEntry.getKey().getShortName(),
                                    subTypeListEntry.getValue()
                                                    .stream()
                                                    .map(e -> new NestedColumnsDTO(e.getId().toString(), e.getName()))
                                                    .collect(Collectors.toList()));
    }

    private TableDataDTO<Map<String, String>> buildRepairCapabilitiesDTO(RepairFormationUnitRepairCapabilityCombinedData combinedData,
                                                                         long rowCount,
                                                                         int pageSize) {
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
                            .flatMap(this::getRepairCapabilitiesNestedColumnsDTO)
                            .collect(Collectors.toList());
        List<RowData<Map<String, String>>> data =
                combinedData.getRepairFormationUnitList()
                            .stream()
                            .map(rs -> getRepairCapabilitiesRow(combinedData.getCalculatedRepairCapabilities(),
                                                                columns,
                                                                rs))
                            .collect(Collectors.toList());
        Long totalPageNum = (pageSize == 0 ? 1 : rowCount / pageSize + (rowCount % pageSize == 0 ? 0 : 1));
        return new TableDataDTO<>(equipmentPerTypeDTOList, data, totalPageNum);
    }

    private RowData<Map<String, String>> getRepairCapabilitiesRow(
            Map<RepairFormationUnit, Map<Equipment, Double>> calculatedRepairCapabilities,
            List<Equipment> columns,
            RepairFormationUnit rs) {
        return new RowData<>(
                rs.getId(),
                rs.getName(),
                columns
                        .stream()
                        .collect(Collectors.toMap(equipment -> equipment.getId().toString(),
                                                  equipment -> Formatter.formatDoubleAsString(
                                                          calculatedRepairCapabilities
                                                                  .getOrDefault(rs, Collections.emptyMap())
                                                                  .getOrDefault(equipment, 0.0)))));
    }

    @GetMapping("/{repairFormationUnitId}/capabilities/repair-type/{repairTypeId}")
    @ResponseBody
    public ResponseEntity<List<EquipmentTypeStaffData>> getCalculatedRepairCapabilitiesForUnit(
            @ApiParam(value = "Ключ РВО", required = true) @PathVariable @Positive Long repairFormationUnitId,
            @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable @Positive Long repairTypeId,
            @ApiParam(value = "Ключи ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @ApiParam(value = "Ключи подтипов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentSubTypeId) {
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> grouped =
                equipmentService.listGroupedByTypes(nullIfEmpty(equipmentId),
                                                    nullIfEmpty(equipmentSubTypeId),
                                                    nullIfEmpty(equipmentTypeId));
        Map<EquipmentSubType, RepairFormationUnitEquipmentStaff> equipmentStaff =
                repairFormationUnitService.getEquipmentStaffPerSubType(tehoSession.getSessionId(),
                                                                       repairFormationUnitId,
                                                                       nullIfEmpty(equipmentTypeId),
                                                                       nullIfEmpty(equipmentSubTypeId));
        Map<Equipment, Double> calculatedRepairCapabilities =
                repairCapabilitiesService.getCalculatedRepairCapabilities(repairFormationUnitId,
                                                                          tehoSession.getSessionId(),
                                                                          repairTypeId,
                                                                          nullIfEmpty(equipmentId),
                                                                          nullIfEmpty(equipmentSubTypeId),
                                                                          nullIfEmpty(equipmentTypeId));
        List<EquipmentTypeStaffData> result = grouped
                .entrySet()
                .stream()
                .map(equipmentTypeListEntry -> {
                    List<EquipmentStaffPerSubType> subTypes =
                            equipmentTypeListEntry
                                    .getValue()
                                    .entrySet()
                                    .stream()
                                    .map(est -> getEquipmentStaffPerSubType(equipmentStaff,
                                                                            calculatedRepairCapabilities,
                                                                            est))
                                    .collect(Collectors.toList());
                    return Optional
                            .ofNullable(equipmentTypeListEntry.getKey())
                            .map(et -> new EquipmentTypeStaffData(et.getId(), et.getFullName(), subTypes))
                            .orElse(new EquipmentTypeStaffData(-1L, subTypes));
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    private EquipmentStaffPerSubType getEquipmentStaffPerSubType(Map<EquipmentSubType, RepairFormationUnitEquipmentStaff> equipmentStaff,
                                                                 Map<Equipment, Double> calculatedRepairCapabilities,
                                                                 Map.Entry<EquipmentSubType, List<Equipment>> est) {
        return new EquipmentStaffPerSubType(
                est.getKey().getId(),
                est.getKey().getFullName(),
                equipmentStaff.getOrDefault(est.getKey(), RepairFormationUnitEquipmentStaff.EMPTY).getTotalStaff(),
                equipmentStaff.getOrDefault(est.getKey(), RepairFormationUnitEquipmentStaff.EMPTY).getAvailableStaff(),
                est
                        .getValue()
                        .stream()
                        .map(e -> new RepairCapabilityPerEquipment(e.getId(),
                                                                   e.getName(),
                                                                   Formatter.formatDouble(calculatedRepairCapabilities.getOrDefault(
                                                                           e,
                                                                           0.0))))
                        .collect(Collectors.toList()));
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
        Long rowCount = repairFormationUnitService.count(repairFormationUnitId);
        TableDataDTO<Map<String, String>> repairCapabilitiesFullDTO = buildRepairCapabilitiesDTO(combinedData,
                                                                                                 rowCount,
                                                                                                 pageSize);
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

        List<RepairFormationUnit> repairFormationUnitList = repairFormationUnitService.list(
                nullIfEmpty(repairFormationUnitId),
                pageNum,
                pageSize);
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> grouped =
                equipmentService.listGroupedByTypes(nullIfEmpty(equipmentId),
                                                    nullIfEmpty(equipmentSubTypeId),
                                                    nullIfEmpty(equipmentTypeId));
        Map<RepairFormationUnit, Map<Equipment, Double>> calculatedRepairCapabilities =
                repairCapabilitiesService.getCalculatedRepairCapabilities(tehoSession.getSessionId(),
                                                                          repairTypeId,
                                                                          nullIfEmpty(repairFormationUnitId),
                                                                          nullIfEmpty(equipmentId),
                                                                          nullIfEmpty(equipmentSubTypeId),
                                                                          nullIfEmpty(equipmentTypeId));
        return new RepairFormationUnitRepairCapabilityCombinedData(
                repairFormationUnitList,
                grouped,
                calculatedRepairCapabilities);
    }

}
