package va.rit.teho.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.CountAndLaborInputDTO;
import va.rit.teho.dto.DistributionIntervalDTO;
import va.rit.teho.dto.LaborDistributionNestedColumnsDTO;
import va.rit.teho.dto.NestedColumnsDTO;
import va.rit.teho.dto.table.LaborDistributionRowData;
import va.rit.teho.dto.table.RowData;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.EquipmentLaborInputDistribution;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.server.TehoSessionData;
import va.rit.teho.service.LaborInputDistributionService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("labor-distribution")
public class LaborInputDistributionController {

    private final LaborInputDistributionService laborInputDistributionService;

    public LaborInputDistributionController(LaborInputDistributionService laborInputDistributionService) {
        this.laborInputDistributionService = laborInputDistributionService;
    }

    @Resource
    private TehoSessionData tehoSession;

    @GetMapping("/intervals")
    public ResponseEntity<List<DistributionIntervalDTO>> getDistributionIntervals() {
        return ResponseEntity.ok(
                laborInputDistributionService
                        .getDistributionIntervals()
                        .stream()
                        .map(DistributionIntervalDTO::from)
                        .collect(Collectors.toList()));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<TableDataDTO<List<RowData<List<LaborDistributionRowData>>>>> getDistributionData(
            @RequestParam(required = false) List<Long> typeId) {
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> laborInputDistribution =
                laborInputDistributionService.getLaborInputDistribution(tehoSession.getSessionId(), typeId);
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
        return ResponseEntity.ok(new TableDataDTO<>(columns, rowData));
    }

    private LaborDistributionRowData getLaborDistributionRowData(EquipmentLaborInputDistribution elid) {
        Map<String, CountAndLaborInputDTO> countAndLaborInputDTOMap =
                elid
                        .getIntervalCountAndLaborInputMap()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> e.getKey().toString(),
                                                  e -> new CountAndLaborInputDTO(
                                                          e.getValue().getCount(),
                                                          e.getValue().getLaborInput())));
        return new LaborDistributionRowData(elid.getBaseName(),
                                            countAndLaborInputDTOMap,
                                            elid.getEquipmentName(),
                                            elid.getAvgDailyFailure(),
                                            elid.getStandardLaborInput(),
                                            elid.getTotalRepairComplexity());
    }

    @PostMapping("/{coefficient}")
    public ResponseEntity<Object> updateDistributionData(@PathVariable Double coefficient) {
        laborInputDistributionService.updateLaborInputDistribution(tehoSession.getSessionId(), coefficient);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/type/{typeId}/{coefficient}")
    public ResponseEntity<Object> updateDistributionDataPerEquipmentType(@PathVariable Long typeId,
                                                                         @PathVariable Double coefficient) {
        laborInputDistributionService.updateLaborInputDistributionPerEquipmentType(tehoSession.getSessionId(),
                                                                                   coefficient,
                                                                                   typeId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/subtype/{subTypeId}/{coefficient}")
    public ResponseEntity<Object> updateDistributionDataPerEquipmentSubType(@PathVariable Long subTypeId,
                                                                            @PathVariable Double coefficient) {
        laborInputDistributionService.updateLaborInputDistributionPerEquipmentSubType(tehoSession.getSessionId(),
                                                                                      coefficient,
                                                                                      subTypeId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/base/{baseId}/{coefficient}")
    public ResponseEntity<Object> updateDistributionDataPerBase(@PathVariable Long baseId,
                                                                @PathVariable Double coefficient) {
        laborInputDistributionService.updateLaborInputDistributionPerBase(tehoSession.getSessionId(),
                                                                          coefficient,
                                                                          baseId);
        return ResponseEntity.accepted().build();
    }

}
