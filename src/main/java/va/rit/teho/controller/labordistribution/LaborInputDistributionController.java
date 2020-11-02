package va.rit.teho.controller.labordistribution;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("labor-distribution")
public class LaborInputDistributionController {

    private static final DecimalFormat FORMATTER = new DecimalFormat("#.###");

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
    public ResponseEntity<TableDataDTO<Map<String, CountAndLaborInputDTO>>> getDistributionData(
            @PathVariable Long stageId,
            @PathVariable Long repairTypeId,
            @RequestParam(required = false) List<Long> equipmentTypeId) {
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
                                                          FORMATTER.format(e.getValue().getCount()),
                                                          FORMATTER.format(e.getValue().getLaborInput()))));
        return new LaborDistributionRowData(elid.getBaseName(),
                                            countAndLaborInputDTOMap,
                                            elid.getEquipmentName(),
                                            FORMATTER.format(elid.getAvgDailyFailure()),
                                            elid.getStandardLaborInput(),
                                            FORMATTER.format(elid.getTotalRepairComplexity()));
    }

    @PostMapping("/{coefficient}")
    public ResponseEntity<Object> updateDistributionData(@PathVariable Double coefficient) {
        equipmentPerBaseService.updateAvgDailyFailureData(tehoSession.getSessionId(), coefficient);
        laborInputDistributionService.updateLaborInputDistribution(tehoSession.getSessionId());
        return ResponseEntity.accepted().build();
    }

}
