package va.rit.teho.controller.repairdivision;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.controller.helper.Formatter;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.RowData;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairdivision.RepairDivisionUnit;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.repairdivision.RepairCapabilitiesService;
import va.rit.teho.service.repairdivision.RepairDivisionService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "repair-capabilities", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Производственные возможности РВО")
public class RepairCapabilitiesController {

    private final RepairCapabilitiesService repairCapabilitiesService;
    private final EquipmentService equipmentService;
    private final RepairDivisionService repairDivisionService;

    public RepairCapabilitiesController(
            RepairCapabilitiesService repairCapabilitiesService,
            EquipmentService equipmentService,
            RepairDivisionService repairDivisionService) {
        this.repairCapabilitiesService = repairCapabilitiesService;
        this.equipmentService = equipmentService;
        this.repairDivisionService = repairDivisionService;
    }

    @Resource
    private TehoSessionData tehoSession;

    /**
     * Расчет производственных возможностей РВО по ремонту (сразу для всех РВО по всем ВВСТ).
     */
    @PostMapping("/repair-type/{id}")
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

    @PostMapping("/repair-type/{id}/repair-division/{repairDivisionUnitId}")
    @ResponseBody
    @ApiOperation(value = "Расчет производственных возможностей РВО по ремонту (для указанного РВО)")
    public ResponseEntity<TableDataDTO<Map<String, String>>> calculateAndGetPerStation(@ApiParam(value = "Ключ типа ремонта, по которому производится расчет", required = true) @PathVariable("id") Long repairTypeId,
                                                                                       @ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairDivisionUnitId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilitiesPerStation(tehoSession.getSessionId(),
                                                                                      repairDivisionUnitId,
                                                                                      repairTypeId);
        TableDataDTO<Map<String, String>> repairCapabilitiesDTO =
                getCalculatedRepairCapabilities(repairTypeId,
                                                Collections.singletonList(repairDivisionUnitId),
                                                Collections.emptyList(),
                                                Collections.emptyList(),
                                                Collections.emptyList(), 1,
                                                100).getBody();
        return ResponseEntity.accepted().body(repairCapabilitiesDTO);
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

    private TableDataDTO<Map<String, String>> buildRepairCapabilitiesDTO(List<RepairDivisionUnit> repairDivisionUnitList,
                                                                         Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> grouped,
                                                                         Map<RepairDivisionUnit, Map<Equipment, Double>> calculatedRepairCapabilities) {
        List<Equipment> columns =
                grouped
                        .values()
                        .stream()
                        .flatMap(l -> l.values().stream())
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
        List<NestedColumnsDTO> equipmentPerTypeDTOList =
                grouped.entrySet()
                       .stream()
                       .map(this::getRepairCapabilitiesNestedColumnsDTO)
                       .collect(Collectors.toList());
        List<RowData<Map<String, String>>> data =
                repairDivisionUnitList
                        .stream()
                        .map(rs -> getRepairCapabilitiesRow(calculatedRepairCapabilities, columns, rs))
                        .collect(Collectors.toList());
        return new TableDataDTO<>(equipmentPerTypeDTOList, data);
    }

    private RowData<Map<String, String>> getRepairCapabilitiesRow(
            Map<RepairDivisionUnit, Map<Equipment, Double>> calculatedRepairCapabilities,
            List<Equipment> columns,
            RepairDivisionUnit rs) {
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

    @GetMapping("/repair-type/{id}")
    @ResponseBody
    @ApiOperation(value = "Получение расчитанных производственных возможностей РВО по ремонту ВВСТ")
    public ResponseEntity<TableDataDTO<Map<String, String>>> getCalculatedRepairCapabilities(
            @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable("id") Long repairTypeId,
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> repairDivisionUnitId,
            @ApiParam(value = "Ключи ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @ApiParam(value = "Ключи подтипов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentSubTypeId,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        List<RepairDivisionUnit> repairDivisionUnitList = repairDivisionService.listUnits(repairDivisionUnitId,
                                                                                          pageNum,
                                                                                          pageSize);
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> grouped =
                equipmentService.listGroupedByTypes(equipmentId,
                                                    equipmentSubTypeId,
                                                    equipmentTypeId);
        Map<RepairDivisionUnit, Map<Equipment, Double>> calculatedRepairCapabilities =
                repairCapabilitiesService.getCalculatedRepairCapabilities(tehoSession.getSessionId(),
                                                                          repairTypeId,
                                                                          repairDivisionUnitId,
                                                                          equipmentId,
                                                                          equipmentSubTypeId,
                                                                          equipmentTypeId);
        TableDataDTO<Map<String, String>> repairCapabilitiesFullDTO = buildRepairCapabilitiesDTO(repairDivisionUnitList,
                                                                                                 grouped,
                                                                                                 calculatedRepairCapabilities);
        return ResponseEntity.ok(repairCapabilitiesFullDTO);
    }

}
