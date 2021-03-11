package va.rit.teho.controller.labordistribution;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.controller.helper.Formatter;
import va.rit.teho.controller.helper.ReportResponseEntity;
import va.rit.teho.dto.labordistribution.CountAndLaborInputDTO;
import va.rit.teho.dto.labordistribution.LaborDistributionFilterData;
import va.rit.teho.dto.labordistribution.LaborDistributionNestedColumnsDTO;
import va.rit.teho.dto.labordistribution.LaborDistributionRowData;
import va.rit.teho.dto.table.GenericTableDataDTO;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;
import va.rit.teho.entity.labordistribution.combined.CountAndLaborInput;
import va.rit.teho.entity.labordistribution.combined.CountAndLaborInputCombinedData;
import va.rit.teho.entity.labordistribution.combined.EquipmentLaborInputDistribution;
import va.rit.teho.entity.labordistribution.combined.LaborInputDistributionCombinedData;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;
import va.rit.teho.service.labordistribution.WorkhoursDistributionIntervalService;
import va.rit.teho.service.report.ReportService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static va.rit.teho.controller.helper.FilterConverter.nullIfEmpty;

@Controller
@Validated
@RequestMapping(path = "labor-distribution", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Распределение ремонтного фонда")
public class LaborInputDistributionController {

    private final EquipmentTypeService equipmentTypeService;
    private final LaborInputDistributionService laborInputDistributionService;
    private final WorkhoursDistributionIntervalService workhoursDistributionIntervalService;
    private final RepairTypeService repairTypeService;

    private final ReportService<LaborInputDistributionCombinedData> reportService;
    private final ReportService<LaborInputDistributionCombinedData> distributionForAllRepairTypesReportService;

    @Resource
    private TehoSessionData tehoSession;

    public LaborInputDistributionController(EquipmentTypeService equipmentTypeService,
                                            LaborInputDistributionService laborInputDistributionService,
                                            WorkhoursDistributionIntervalService workhoursDistributionIntervalService,
                                            RepairTypeService repairTypeService,
                                            @Qualifier("oneRepairType") ReportService<LaborInputDistributionCombinedData> reportService,
                                            @Qualifier("allRepairTypes") ReportService<LaborInputDistributionCombinedData> distributionForAllRepairTypesReportService) {
        this.equipmentTypeService = equipmentTypeService;
        this.laborInputDistributionService = laborInputDistributionService;
        this.workhoursDistributionIntervalService = workhoursDistributionIntervalService;
        this.repairTypeService = repairTypeService;
        this.reportService = reportService;
        this.distributionForAllRepairTypesReportService = distributionForAllRepairTypesReportService;
    }

    @GetMapping("/stage/{stageId}/repair-type/{repairTypeId}/report")
    @ResponseBody
    @Transactional
    @ApiOperation(value = "Получить данные о распределении ремонтного фонда подразделения по трудоемкости ремонта (в табличном формате)")
    public ResponseEntity<byte[]> getDistributionDataReport(
            @ApiParam(value = "Ключ этапа", required = true) @PathVariable @Positive Long stageId,
            @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable @Positive Long repairTypeId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId) throws
            UnsupportedEncodingException {
        Map<EquipmentType, List<EquipmentLaborInputDistribution>> laborInputDistribution =
                laborInputDistributionService.getLaborInputDistribution(tehoSession.getSessionId(),
                                                                        repairTypeId,
                                                                        stageId,
                                                                        nullIfEmpty(equipmentTypeId));
        List<WorkhoursDistributionInterval> distributionIntervals = workhoursDistributionIntervalService.listSorted();

        byte[] bytes = reportService.generateReport(new LaborInputDistributionCombinedData(
                equipmentTypeService.listHighestLevelTypes(equipmentTypeId),
                Collections.emptyList(),
                laborInputDistribution,
                distributionIntervals));

        return ReportResponseEntity.ok("Распределение производственного фонда", bytes);
    }

    @GetMapping("/stage/{stageId}/repair-type/{repairTypeId}")
    @ResponseBody
    @ApiOperation(value = "Получить данные о распределении ремонтного фонда подразделения по трудоемкости ремонта (в табличном формате)")
    public ResponseEntity<GenericTableDataDTO<Map<String, CountAndLaborInputDTO>, LaborDistributionRowData<CountAndLaborInputDTO>>> getDistributionData(
            @ApiParam(value = "Ключ этапа", required = true) @PathVariable @Positive Long stageId,
            @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable @Positive Long repairTypeId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId) {
        Map<EquipmentType, List<EquipmentLaborInputDistribution>> laborInputDistribution =
                laborInputDistributionService.getLaborInputDistribution(tehoSession.getSessionId(),
                        repairTypeId,
                        stageId,
                        nullIfEmpty(equipmentTypeId));
        List<NestedColumnsDTO> columns =
                workhoursDistributionIntervalService
                        .listSorted()
                        .stream()
                        .map(wdi -> new LaborDistributionNestedColumnsDTO(wdi.getId(),
                                wdi.getLowerBound(),
                                wdi.getUpperBound(),
                                true))
                        .collect(Collectors.toList());
        List<LaborDistributionRowData<CountAndLaborInputDTO>> rows =
                laborInputDistribution
                        .entrySet()
                        .stream()
                        .flatMap(rd -> rd.getValue().stream()
                                         .map(this::getLaborDistributionRowData))
                        .collect(Collectors.toList());
        return ResponseEntity.ok(new GenericTableDataDTO<>(columns, rows));
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(value = "Получить данные о распределении ремонтного фонда подразделения по трудоемкости ремонта по всем типам ремонта (в табличном формате)")
    public ResponseEntity<GenericTableDataDTO<Map<String, String>, LaborDistributionRowData<String>>> getDistributionDataForAllRepairTypes(@RequestParam(required = false) List<Long> formationIds,
                                                                                                                                           @RequestParam(required = false) List<Long> equipmentIds) {
        Map<EquipmentType, List<EquipmentLaborInputDistribution>> aggregatedLaborInputDistribution =
                laborInputDistributionService.getAggregatedLaborInputDistribution(tehoSession.getSessionId(),
                        nullIfEmpty(formationIds),
                        nullIfEmpty(equipmentIds));
        List<RepairType> repairTypes = repairTypeService.list(true);
        repairTypes.sort(Comparator.comparing(RepairType::getShortName).reversed());
        List<NestedColumnsDTO> columns =
                repairTypes
                        .stream()
                        .map(this::buildDistributionNestedColumnsPerRepairType)
                        .collect(Collectors.toList());
        List<LaborDistributionRowData<String>> rows =
                aggregatedLaborInputDistribution
                        .entrySet()
                        .stream()
                        .flatMap(rd -> rd.getValue().stream().map(v -> buildRowDataPerRepairTypes(v, repairTypes)))
                        .collect(Collectors.toList());
        return ResponseEntity.ok(new GenericTableDataDTO<>(columns, rows));
    }

    @GetMapping("/report")
    @ResponseBody
    @Transactional
    @ApiOperation(value = "Получить данные о распределении ремонтного фонда подразделения по трудоемкости ремонта по всем типам ремонта (в табличном формате)")
    public ResponseEntity<byte[]> getDistributionDataForAllRepairTypesReport(@RequestParam(required = false) List<Long> formationIds,
                                                                             @RequestParam(required = false) List<Long> equipmentIds) throws
            UnsupportedEncodingException {
        Map<EquipmentType, List<EquipmentLaborInputDistribution>> aggregatedLaborInputDistribution =
                laborInputDistributionService.getAggregatedLaborInputDistribution(tehoSession.getSessionId(),
                                                                                  nullIfEmpty(formationIds),
                                                                                  nullIfEmpty(equipmentIds));
        List<RepairType> repairTypes = repairTypeService.list(true);

        byte[] bytes = distributionForAllRepairTypesReportService.generateReport(new LaborInputDistributionCombinedData(
                equipmentTypeService.listHighestLevelTypes(null),
                repairTypes,
                aggregatedLaborInputDistribution,
                workhoursDistributionIntervalService.listSorted()));

        return ReportResponseEntity.ok("Распределение производственного фонда (по всем типам ремонта)", bytes);
    }

    private NestedColumnsDTO buildDistributionNestedColumnsPerRepairType(RepairType rt) {
        List<NestedColumnsDTO> intervalColumns =
                workhoursDistributionIntervalService
                        .listSorted()
                        .stream()
                        .map(wdi -> new LaborDistributionNestedColumnsDTO(buildCombinedKey(rt,
                                                                                           wdi.getId()),
                                                                          wdi.getLowerBound(),
                                                                          wdi.getUpperBound(),
                                                                          false))
                        .collect(Collectors.toList());
        if (rt.includesIntervals()) {
            return new NestedColumnsDTO(rt.getShortName(), intervalColumns);
        } else {
            return new NestedColumnsDTO("rt_" + rt.getId(), rt.getShortName());
        }
    }

    private String buildCombinedKey(RepairType rt, Long wdiId) {
        return "rt_" + rt.getId() + "_i_" + wdiId;
    }

    private LaborDistributionRowData<CountAndLaborInputDTO> getLaborDistributionRowData(EquipmentLaborInputDistribution elid) {
        Map<String, CountAndLaborInputDTO> countAndLaborInputDTOMap =
                elid
                        .getCountAndLaborInputCombinedData()
                        .entrySet()
                        .stream()
                        .flatMap(repairTypeCountAndLaborInputCombinedDataEntry -> repairTypeCountAndLaborInputCombinedDataEntry
                                .getValue()
                                .getCountAndLaborInputMap()
                                .entrySet()
                                .stream())
                        .collect(Collectors.toMap(e -> e.getKey().toString(),
                                                  e -> new CountAndLaborInputDTO(
                                                          Formatter.formatDoubleAsString(e.getValue().getCount()),
                                                          Formatter.formatDoubleAsString(e
                                                                                                 .getValue()
                                                                                                 .getLaborInput()))));
        return new LaborDistributionRowData<>(elid.getFormationName(),
                                              countAndLaborInputDTOMap,
                                              elid.getEquipmentName(),
                                              elid.getEquipmentAmount(),
                                              Formatter.formatDoubleAsString(elid.getAvgDailyFailure()),
                                              elid.getStandardLaborInput(),
                                              Formatter.formatDoubleAsString(elid.getTotalRepairComplexity()));
    }

    @PostMapping
    @ApiOperation(value = "Обновить данные о распределении ремонтного фонда подразделения по трудоемкости ремонта")
    public ResponseEntity<Object> updateDistributionData(@Valid @RequestBody LaborDistributionFilterData filterData) {
        laborInputDistributionService.updateLaborInputDistribution(tehoSession.getSessionId(),
                                                                   nullIfEmpty(filterData.getEquipmentIds()),
                                                                   nullIfEmpty(filterData.getFormationIds()));
        return ResponseEntity.accepted().build();
    }

    private LaborDistributionRowData<String> buildRowDataPerRepairTypes(EquipmentLaborInputDistribution elid,
                                                                        List<RepairType> repairTypes) {
        Map<String, String> countMap = new HashMap<>();
        repairTypes.forEach(repairType -> {
            CountAndLaborInputCombinedData countAndLaborInputCombinedData = elid
                    .getCountAndLaborInputCombinedData()
                    .getOrDefault(repairType, CountAndLaborInputCombinedData.EMPTY);
            Map<Long, CountAndLaborInput> countAndLaborInputMap = countAndLaborInputCombinedData
                    .getCountAndLaborInputMap();
            if (!countAndLaborInputMap.isEmpty()) {
                countAndLaborInputMap
                        .forEach((key, countAndLaborInput) -> countMap.put(
                                buildCombinedKey(repairType, key),
                                Formatter.formatDoubleAsString(
                                        countAndLaborInput.getCount())));
            }
            countMap.put("rt_" + repairType.getId(),
                         Formatter.formatDoubleAsString(countAndLaborInputCombinedData.getTotalFailureAmount()));
        });
        return new LaborDistributionRowData<>(
                elid.getFormationName(),
                countMap,
                elid.getEquipmentName(),
                elid.getEquipmentAmount(),
                Formatter.formatDoubleAsString(elid.getAvgDailyFailure()),
                elid.getStandardLaborInput(),
                Formatter.formatDoubleAsString(elid.getTotalRepairComplexity()));
    }
}
