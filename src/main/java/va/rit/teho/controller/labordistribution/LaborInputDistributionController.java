package va.rit.teho.controller.labordistribution;

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
import va.rit.teho.dto.labordistribution.CountAndLaborInputDTO;
import va.rit.teho.dto.labordistribution.LaborDistributionFilterData;
import va.rit.teho.dto.labordistribution.LaborDistributionNestedColumnsDTO;
import va.rit.teho.dto.labordistribution.LaborDistributionRowData;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.EquipmentLaborInputDistribution;
import va.rit.teho.entity.labordistribution.LaborInputDistributionCombinedData;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;
import va.rit.teho.service.report.ReportService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static va.rit.teho.controller.helper.FilterConverter.nullIfEmpty;

@Controller
@Validated
@RequestMapping(path = "labor-distribution", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Распределение ремонтного фонда")
public class LaborInputDistributionController {

    private final LaborInputDistributionService laborInputDistributionService;

    private final ReportService<LaborInputDistributionCombinedData> reportService;

    @Resource
    private TehoSessionData tehoSession;

    public LaborInputDistributionController(LaborInputDistributionService laborInputDistributionService,
                                            ReportService<LaborInputDistributionCombinedData> reportService) {
        this.laborInputDistributionService = laborInputDistributionService;
        this.reportService = reportService;
    }

    @GetMapping("/stage/{stageId}/repair-type/{repairTypeId}/report")
    @ResponseBody
    @ApiOperation(value = "Получить данные о распределении ремонтного фонда подразделения по трудоемкости ремонта (в табличном формате)")
    public ResponseEntity<byte[]> getDistributionDataReport(
            @ApiParam(value = "Ключ этапа", required = true) @PathVariable @Positive Long stageId,
            @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable @Positive Long repairTypeId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId) throws
            UnsupportedEncodingException {
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> laborInputDistribution =
                laborInputDistributionService.getLaborInputDistribution(tehoSession.getSessionId(),
                                                                        repairTypeId,
                                                                        stageId,
                                                                        nullIfEmpty(equipmentTypeId));
        List<WorkhoursDistributionInterval> distributionIntervals =
                laborInputDistributionService.listDistributionIntervals();

        byte[] bytes = reportService.generateReport(new LaborInputDistributionCombinedData(laborInputDistribution,
                                                                                           distributionIntervals));

        return ReportResponseEntity.ok("Распределение производственного фонда", bytes);
    }

    @GetMapping("/stage/{stageId}/repair-type/{repairTypeId}")
    @ResponseBody
    @ApiOperation(value = "Получить данные о распределении ремонтного фонда подразделения по трудоемкости ремонта (в табличном формате)")
    public ResponseEntity<TableDataDTO<Map<String, CountAndLaborInputDTO>>> getDistributionData(
            @ApiParam(value = "Ключ этапа", required = true) @PathVariable @Positive Long stageId,
            @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable @Positive Long repairTypeId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId) {
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> laborInputDistribution =
                laborInputDistributionService.getLaborInputDistribution(tehoSession.getSessionId(),
                                                                        repairTypeId,
                                                                        stageId,
                                                                        nullIfEmpty(equipmentTypeId));
        List<NestedColumnsDTO> columns =
                laborInputDistributionService
                        .listDistributionIntervals()
                        .stream()
                        .sorted(Comparator.comparing(WorkhoursDistributionInterval::getLowerBound,
                                                     Comparator.nullsFirst(Comparator.naturalOrder())))
                        .map(wdi -> new LaborDistributionNestedColumnsDTO(wdi.getId(),
                                                                          wdi.getLowerBound(),
                                                                          wdi.getUpperBound()))
                        .collect(Collectors.toList());
        List<LaborDistributionRowData> rows =
                laborInputDistribution
                        .entrySet()
                        .stream()
                        .flatMap(rd -> rd.getValue().values().stream()
                                         .flatMap(l -> l.stream().map(this::getLaborDistributionRowData)))
                        .collect(Collectors.toList());
        return ResponseEntity.ok(new TableDataDTO<>(columns, rows));
    }

    private LaborDistributionRowData getLaborDistributionRowData(EquipmentLaborInputDistribution elid) {
        Map<String, CountAndLaborInputDTO> countAndLaborInputDTOMap =
                elid
                        .getIntervalCountAndLaborInputMap()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> e.getKey().toString(),
                                                  e -> new CountAndLaborInputDTO(
                                                          Formatter.formatDoubleAsString(e.getValue().getCount()),
                                                          Formatter.formatDoubleAsString(e
                                                                                                 .getValue()
                                                                                                 .getLaborInput()))));
        return new LaborDistributionRowData(elid.getFormationName(),
                                            countAndLaborInputDTOMap,
                                            elid.getEquipmentName(),
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

}
