package va.rit.teho.controller.labordistribution;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.controller.helper.Formatter;
import va.rit.teho.dto.labordistribution.CountAndLaborInputDTO;
import va.rit.teho.dto.labordistribution.LaborDistributionNestedColumnsDTO;
import va.rit.teho.dto.labordistribution.LaborDistributionRowData;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.RowData;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.EquipmentLaborInputDistribution;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.equipment.EquipmentPerBaseService;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "labor-distribution", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Распределение ремонтного фонда")
public class LaborInputDistributionController {

    private final EquipmentPerBaseService equipmentPerBaseService;
    private final LaborInputDistributionService laborInputDistributionService;

    public LaborInputDistributionController(EquipmentPerBaseService equipmentPerBaseService,
                                            LaborInputDistributionService laborInputDistributionService) {
        this.equipmentPerBaseService = equipmentPerBaseService;
        this.laborInputDistributionService = laborInputDistributionService;
    }

    @Resource
    private TehoSessionData tehoSession;


    @GetMapping("/stage/{stageId}/repair-type/{repairTypeId}")
    @ResponseBody
    @ApiOperation(value = "Получить данные о распределении ремонтного фонда подразделения по трудоемкости ремонта (в табличном формате)")
    public ResponseEntity<TableDataDTO<Map<String, CountAndLaborInputDTO>>> getDistributionData(
            @ApiParam(value = "Ключ этапа", required = true) @PathVariable Long stageId,
            @ApiParam(value = "Ключ типа ремонта", required = true) @PathVariable Long repairTypeId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId) {
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> laborInputDistribution =
                laborInputDistributionService.getLaborInputDistribution(tehoSession.getSessionId(),
                                                                        repairTypeId,
                                                                        stageId,
                                                                        equipmentTypeId);
        List<NestedColumnsDTO> columns =
                laborInputDistributionService
                        .getDistributionIntervals()
                        .stream()
                        .map(wdi -> new LaborDistributionNestedColumnsDTO(wdi.getId(),
                                                                          wdi.getLowerBound(),
                                                                          wdi.getUpperBound()))
                        .collect(Collectors.toList());
        List<RowData<List<RowData<List<LaborDistributionRowData>>>>> rowData = laborInputDistribution
                .entrySet()
                .stream()
                .map(eTypeEntry ->
                             new RowData<>(
                                     eTypeEntry.getKey().getFullName(),
                                     eTypeEntry
                                             .getValue()
                                             .entrySet()
                                             .stream()
                                             .map(eSubTypeEntry ->
                                                          new RowData<>(
                                                                  eSubTypeEntry.getKey().getFullName(),
                                                                  eSubTypeEntry
                                                                          .getValue()
                                                                          .stream()
                                                                          .map(this::getLaborDistributionRowData)
                                                                          .collect(Collectors.toList())))
                                             .collect(Collectors.toList())))
                .collect(Collectors.toList());
        //TODO: Вернуть группировку по типам и подтипам когда будет готова UI-часть
        List<LaborDistributionRowData> collect = rowData.stream().flatMap(rd -> rd
                .getData()
                .stream()
                .flatMap(rdi -> rdi.getData().stream())).collect(Collectors.toList());
        return ResponseEntity.ok(new TableDataDTO<>(columns, collect));
    }

    private LaborDistributionRowData getLaborDistributionRowData(EquipmentLaborInputDistribution elid) {
        Map<String, CountAndLaborInputDTO> countAndLaborInputDTOMap =
                elid
                        .getIntervalCountAndLaborInputMap()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> e.getKey().toString(),
                                                  e -> new CountAndLaborInputDTO(
                                                          Formatter.formatDouble(e.getValue().getCount()),
                                                          Formatter.formatDouble(e.getValue().getLaborInput()))));
        return new LaborDistributionRowData(elid.getBaseName(),
                                            countAndLaborInputDTOMap,
                                            elid.getEquipmentName(),
                                            Formatter.formatDouble(elid.getAvgDailyFailure()),
                                            elid.getStandardLaborInput(),
                                            Formatter.formatDouble(elid.getTotalRepairComplexity()));
    }

    @PostMapping("/{coefficient}")
    @ApiOperation(value = "Обновить данные о распределении ремонтного фонда подразделения по трудоемкости ремонта")
    public ResponseEntity<Object> updateDistributionData(@ApiParam(value = "Коэффициент (k), используемый в расчетах", required = true) @PathVariable Double coefficient) {
        equipmentPerBaseService.updateAvgDailyFailureData(tehoSession.getSessionId(), coefficient);
        laborInputDistributionService.updateLaborInputDistribution(tehoSession.getSessionId());
        return ResponseEntity.accepted().build();
    }

}
