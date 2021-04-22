package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.controller.helper.Paginator;
import va.rit.teho.controller.helper.ReportResponseEntity;
import va.rit.teho.dto.repairformation.EquipmentTypeStaffData;
import va.rit.teho.dto.repairformation.RepairCapabilityPerEquipment;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.RowData;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapability;
import va.rit.teho.entity.repairformation.combined.RepairFormationUnitRepairCapabilityCombinedData;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.repairformation.RepairCapabilitiesService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;
import va.rit.teho.service.report.ReportService;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static va.rit.teho.controller.helper.FilterConverter.nullIfEmpty;

@Controller
@Validated
@RequestMapping(path = "formation/repair-formation/unit", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Производственные возможности РВО")
public class RepairCapabilitiesController {

    private final RepairCapabilitiesService repairCapabilitiesService;
    private final EquipmentTypeService equipmentTypeService;
    private final RepairFormationUnitService repairFormationUnitService;
    private final ReportService<RepairFormationUnitRepairCapabilityCombinedData> reportService;

    @Resource
    private TehoSessionData tehoSession;

    public RepairCapabilitiesController(
            RepairCapabilitiesService repairCapabilitiesService,
            EquipmentTypeService equipmentTypeService,
            RepairFormationUnitService repairFormationUnitService,
            ReportService<RepairFormationUnitRepairCapabilityCombinedData> reportService) {
        this.repairCapabilitiesService = repairCapabilitiesService;
        this.equipmentTypeService = equipmentTypeService;
        this.repairFormationUnitService = repairFormationUnitService;
        this.reportService = reportService;
    }

    /**
     * Расчет производственных возможностей РВО по ремонту (сразу для всех РВО по всем ВВСТ).
     */
    @PostMapping(path = "/capabilities/repair-type/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Transactional
    @ApiOperation(value = "Расчет производственных возможностей РВО по ремонту (для всех РВО по всем ВВСТ)")
    public ResponseEntity<TableDataDTO<Map<String, String>>> calculateAndGet(@ApiParam(value = "Ключ типа ремонта, по которому производится расчет", required = true) @PathVariable("id") Long repairTypeId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilities(tehoSession.getSessionId(),
                                                                            repairTypeId);
        return getCalculatedRepairCapabilities(repairTypeId,
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

    private Stream<NestedColumnsDTO> getRepairCapabilitiesNestedColumnsDTO(EquipmentType equipmentType,
                                                                           List<Long> equipmentIds) {
        Set<EquipmentType> subTypes = equipmentType.getEquipmentTypes();
        Set<Equipment> equipmentSet = equipmentType
                .getEquipmentSet()
                .stream()
                .filter(e -> CollectionUtils.isEmpty(equipmentIds) || equipmentIds.contains(e.getId()))
                .collect(Collectors.toSet());
        if (subTypes.isEmpty() && equipmentSet.isEmpty()) {
            return Stream.empty();
        } else {
            Stream<NestedColumnsDTO> equipmentSubColumns =
                    equipmentSet.stream().map(e -> new NestedColumnsDTO(e.getId().toString(), e.getName()));
            Stream<NestedColumnsDTO> subTypesColumns =
                    subTypes.stream().flatMap(st -> getRepairCapabilitiesNestedColumnsDTO(st, equipmentIds));
            return Stream.of(new NestedColumnsDTO(equipmentType.getShortName(),
                                                  Stream
                                                          .concat(equipmentSubColumns, subTypesColumns)
                                                          .collect(Collectors.toList())));
        }
    }

    private TableDataDTO<Map<String, String>> buildRepairCapabilitiesDTO(RepairFormationUnitRepairCapabilityCombinedData combinedData,
                                                                         long rowCount,
                                                                         int pageSize) {
        List<NestedColumnsDTO> equipmentPerTypeNestedColumns =
                combinedData.getEquipmentTypes()
                            .stream()
                            .flatMap(et -> getRepairCapabilitiesNestedColumnsDTO(et, combinedData.getEquipmentIds()))
                            .collect(Collectors.toList());

        List<Equipment> filteredEquipmentList =
                combinedData.getEquipmentTypes()
                            .stream()
                            .flatMap(et -> CollectionUtils.isEmpty(combinedData.getEquipmentIds()) ?
                                    et.collectRelatedEquipment() :
                                    et.collectRelatedEquipment(combinedData.getEquipmentIds()))
                            .collect(Collectors.toList());

        List<RowData<Map<String, String>>> data =
                combinedData.getRepairFormationUnitList()
                            .stream()
                            .map(rs -> getRepairCapabilitiesRow(combinedData.getCalculatedRepairCapabilities(),
                                                                filteredEquipmentList,
                                                                rs))
                            .collect(Collectors.toList());
        return new TableDataDTO<>(equipmentPerTypeNestedColumns, data, Paginator.getPageNum(pageSize, rowCount));
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
                                equipment -> calculatedRepairCapabilities
                                        .getOrDefault(rs, Collections.emptyMap())
                                        .getOrDefault(equipment, 0.0)
                                        .toString())));
    }

    @GetMapping("/{repairFormationUnitId}/capabilities/repair-type/{repairTypeId}")
    @ResponseBody
    @Transactional
    @ApiOperation(value = "Получить производственные возможности данного РВО по ремонту ВВСТ (для конкретного типа ремонта)")
    public ResponseEntity<List<EquipmentTypeStaffData>> getCalculatedRepairCapabilitiesForUnit(
            @ApiParam(value = "Ключ РВО", required = true) @PathVariable @Positive Long repairFormationUnitId,
            @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable @Positive Long repairTypeId,
            @ApiParam(value = "Ключи ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId) {
        List<Long> filteredEquipmentIds = nullIfEmpty(equipmentId);
        List<Long> filteredEquipmentTypeIds = nullIfEmpty(equipmentTypeId);
        List<EquipmentType> equipmentTypes =
                filteredEquipmentTypeIds == null ?
                        equipmentTypeService.listHighestLevelTypes(null) :
                        equipmentTypeService.listTypes(filteredEquipmentTypeIds);
        Map<EquipmentType, RepairFormationUnitEquipmentStaff> equipmentStaff =
                repairFormationUnitService.getEquipmentStaffPerType(tehoSession.getSessionId(),
                                                                    repairFormationUnitId,
                                                                    filteredEquipmentTypeIds);
        Map<Equipment, Double> calculatedRepairCapabilities =
                repairCapabilitiesService.getCalculatedRepairCapabilities(
                        tehoSession.getSessionId(),
                        repairFormationUnitId,
                        repairTypeId,
                        filteredEquipmentIds,
                        filteredEquipmentTypeIds);

        List<EquipmentTypeStaffData> result =
                getEquipmentTypeStaffData(equipmentTypes,
                                          equipmentStaff,
                                          calculatedRepairCapabilities)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    private Stream<EquipmentTypeStaffData> getEquipmentTypeStaffData(Collection<EquipmentType> equipmentTypes,
                                                                     Map<EquipmentType, RepairFormationUnitEquipmentStaff> equipmentStaff,
                                                                     Map<Equipment, Double> calculatedRepairCapabilities) {
        return equipmentTypes
                .stream()
                .map(equipmentType -> {
                    RepairFormationUnitEquipmentStaff staff = equipmentStaff.getOrDefault(equipmentType,
                                                                                          RepairFormationUnitEquipmentStaff.EMPTY);
                    return new EquipmentTypeStaffData(
                            equipmentType.getId(),
                            equipmentType.getShortName(),
                            staff.getTotalStaff(),
                            staff.getAvailableStaff(),
                            getEquipmentTypeStaffData(equipmentType.getEquipmentTypes(),
                                                      equipmentStaff,
                                                      calculatedRepairCapabilities).collect(Collectors.toList()),
                            equipmentType
                                    .getEquipmentSet()
                                    .stream()
                                    .map(equipment -> new RepairCapabilityPerEquipment(equipment.getId(),
                                            equipment.getName(),
                                            calculatedRepairCapabilities.getOrDefault(equipment, 0.0)))
                                    .collect(Collectors.toList()));
                });
    }


    @GetMapping("/capabilities/repair-type/{id}")
    @ResponseBody
    @Transactional
    @ApiOperation(value = "Получение расчитанных производственных возможностей РВО по ремонту ВВСТ")
    public ResponseEntity<TableDataDTO<Map<String, String>>> getCalculatedRepairCapabilities(
            @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable("id") Long repairTypeId,
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> repairFormationUnitId,
            @ApiParam(value = "Ключи ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        RepairFormationUnitRepairCapabilityCombinedData combinedData = getCapabilityCombinedData(
                repairTypeId,
                repairFormationUnitId,
                equipmentId,
                equipmentTypeId,
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
    @Transactional
    @ApiOperation(value = "Получение расчитанных производственных возможностей РВО по ремонту ВВСТ")
    public ResponseEntity<byte[]> getCalculatedRepairCapabilitiesReport(
            @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable("id") Long repairTypeId,
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> repairFormationUnitId,
            @ApiParam(value = "Ключи ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) throws UnsupportedEncodingException {
        RepairFormationUnitRepairCapabilityCombinedData combinedData = getCapabilityCombinedData(
                repairTypeId,
                repairFormationUnitId,
                equipmentId,
                equipmentTypeId,
                pageNum,
                pageSize);

        return ReportResponseEntity.ok("Производственные возможности", reportService.generateReport(combinedData));
    }

    private RepairFormationUnitRepairCapabilityCombinedData getCapabilityCombinedData(Long repairTypeId,
                                                                                      List<Long> repairFormationUnitId,
                                                                                      List<Long> equipmentId,
                                                                                      List<Long> equipmentTypeId,
                                                                                      int pageNum,
                                                                                      int pageSize) {
        List<RepairFormationUnit> repairFormationUnitList = repairFormationUnitService.list(
                nullIfEmpty(repairFormationUnitId),
                pageNum,
                pageSize);
        List<Long> equipmentTypeIdsFilter = nullIfEmpty(equipmentTypeId);
        Map<RepairFormationUnit, Map<Equipment, Double>> calculatedRepairCapabilities =
                repairCapabilitiesService.getCalculatedRepairCapabilities(tehoSession.getSessionId(),
                                                                          repairTypeId,
                                                                          nullIfEmpty(repairFormationUnitId),
                                                                          nullIfEmpty(equipmentId),
                                                                          equipmentTypeIdsFilter);
        return new RepairFormationUnitRepairCapabilityCombinedData(
                repairFormationUnitList,
                Optional
                        .ofNullable(equipmentTypeIdsFilter)
                        .map(equipmentTypeService::listTypes)
                        .orElse(equipmentTypeService.listHighestLevelTypes(null)),
                equipmentId,
                calculatedRepairCapabilities);
    }

}
