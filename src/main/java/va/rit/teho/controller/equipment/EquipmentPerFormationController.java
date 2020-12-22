package va.rit.teho.controller.equipment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.equipment.EquipmentFailureIntensityRowData;
import va.rit.teho.dto.equipment.EquipmentPerFormationSaveDTO;
import va.rit.teho.dto.equipment.IntensityAndAmountDTO;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensity;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.common.StageService;
import va.rit.teho.service.equipment.EquipmentPerFormationService;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "ВВСТ в Формированиях")
public class EquipmentPerFormationController {

    private final StageService stageService;
    private final RepairTypeService repairTypeService;
    private final EquipmentPerFormationService equipmentPerFormationService;

    @Resource
    private TehoSessionData tehoSession;

    public EquipmentPerFormationController(StageService stageService,
                                           RepairTypeService repairTypeService,
                                           EquipmentPerFormationService equipmentPerFormationService) {
        this.stageService = stageService;
        this.repairTypeService = repairTypeService;
        this.equipmentPerFormationService = equipmentPerFormationService;
    }


    @PostMapping(path = "/formation/{formationId}/equipment/{equipmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавить ВВСТ в Формирование")
    public ResponseEntity<Object> addEquipmentToFormation(@ApiParam(value = "Ключ ВЧ", required = true, example = "1") @PathVariable Long formationId,
                                                          @ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable Long equipmentId,
                                                          @ApiParam(value = "Количество ВВСТ в ВЧ и интенсивность выхода в ремонт", required = true) @RequestBody IntensityAndAmountDTO intensityAndAmount) {
        equipmentPerFormationService.addEquipmentToFormation(formationId,
                                                             equipmentId,
                                                             (long) intensityAndAmount.getAmount());
        intensityAndAmount
                .getIntensity()
                .forEach(intensityPerRepairTypeAndStageDTO ->
                                 equipmentPerFormationService.setEquipmentPerFormationFailureIntensity(
                                         tehoSession.getSessionId(),
                                         formationId,
                                         equipmentId,
                                         intensityPerRepairTypeAndStageDTO.getRepairTypeId(),
                                         intensityPerRepairTypeAndStageDTO.getStageId(),
                                         intensityPerRepairTypeAndStageDTO.getIntensity()));
        return ResponseEntity.accepted().build();
    }

    @PutMapping(path = "/formation/{formationId}/equipment/{equipmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить ВВСТ в Формировании")
    public ResponseEntity<Object> updateEquipmentInFormation(@ApiParam(value = "Ключ ВЧ", required = true, example = "1") @PathVariable Long formationId,
                                                             @ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable Long equipmentId,
                                                             @ApiParam(value = "Количество ВВСТ в ВЧ и интенсивность выхода в ремонт", required = true) @RequestBody
                                                                     EquipmentPerFormationSaveDTO equipmentPerFormationSaveDTO) {
        equipmentPerFormationService.updateEquipmentInFormation(formationId,
                                                                equipmentId,
                                                                equipmentPerFormationSaveDTO.getAmount());
        equipmentPerFormationSaveDTO.getData().forEach((stageId, repairTypeIntensityMap) ->
                                                               repairTypeIntensityMap
                                                                       .forEach((repairTypeId, intensity) ->
                                                                                        equipmentPerFormationService
                                                                                                .setEquipmentPerFormationFailureIntensity(
                                                                                                        tehoSession
                                                                                                                .getSessionId(),
                                                                                                        formationId,
                                                                                                        equipmentId,
                                                                                                        repairTypeId,
                                                                                                        stageId,
                                                                                                        intensity)));
        return ResponseEntity.accepted().build();
    }


    @GetMapping("/formation/{formationId}/equipment")
    @ResponseBody
    @ApiOperation(value = "Получить данные о ВВСТ в Формированиях (в табличном виде)")
    public TableDataDTO<Map<String, Map<String, String>>> getEquipmentPerFormationData(
            @ApiParam(value = "Ключ ВЧ", required = true, example = "1")
            @PathVariable Long formationId,
            @RequestParam(required = false) List<Long> equipmentIds) {
        return this.getEquipmentRowData(formationId,
                                        EquipmentPerFormationFailureIntensity::getIntensityPercentage,
                                        0,
                                        Object::toString,
                                        equipmentIds);
    }

    private <T> TableDataDTO<Map<String, Map<String, String>>> getEquipmentRowData(Long formationId,
                                                                                   Function<EquipmentPerFormationFailureIntensity, T> getter,
                                                                                   T defaultValue,
                                                                                   Function<T, String> formatter,
                                                                                   List<Long> equipmentIds) {
        Map<Pair<Formation, Equipment>, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>> failureIntensityData =
                equipmentPerFormationService.getFailureIntensityData(tehoSession.getSessionId(),
                                                                     formationId,
                                                                     equipmentIds);
        return getTableDataDTO(formationId,
                               (epb) -> Pair.of(epb.getFormation(), epb.getEquipment()),
                               getter,
                               defaultValue,
                               formatter,
                               failureIntensityData,
                               equipmentIds);
    }

    private <K, T> TableDataDTO<Map<String, Map<String, String>>> getTableDataDTO(Long formationId,
                                                                                  Function<EquipmentPerFormation, K> keyGetter,
                                                                                  Function<EquipmentPerFormationFailureIntensity, T> getter,
                                                                                  T defaultValue,
                                                                                  Function<T, String> formatter,
                                                                                  Map<K, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>> failureIntensityData,
                                                                                  List<Long> equipmentIds) {
        List<Stage> stages = stageService.list();
        List<RepairType> repairTypes = repairTypeService.list(true);

        List<NestedColumnsDTO> stageColumns = new ArrayList<>();
        for (Stage s : stages) {
            List<NestedColumnsDTO> repairTypeColumns = new ArrayList<>();
            for (RepairType rt : repairTypes) {
                NestedColumnsDTO repairTypeColumn =
                        new NestedColumnsDTO(Arrays.asList(s.getId().toString(), rt.getId().toString()),
                                             rt.getShortName());
                repairTypeColumns.add(repairTypeColumn);
            }
            NestedColumnsDTO nestedColumnsDTO = new NestedColumnsDTO(s.getId().toString(), repairTypeColumns);
            stageColumns.add(nestedColumnsDTO);
        }

        List<EquipmentFailureIntensityRowData<String>> rowData =
                (formationId == null ? equipmentPerFormationService.getTotalEquipmentInFormations(equipmentIds) :
                        equipmentPerFormationService.getEquipmentInFormation(formationId, equipmentIds))
                        .stream()
                        .map(epb -> getEquipmentFailureIntensityRowData(failureIntensityData,
                                                                        keyGetter,
                                                                        stages,
                                                                        repairTypes,
                                                                        epb,
                                                                        getter,
                                                                        defaultValue,
                                                                        formatter))
                        .collect(Collectors.toList());
        return new TableDataDTO<>(stageColumns, rowData);
    }

    @GetMapping("/formation/equipment/daily-failure")
    @ResponseBody
    public TableDataDTO<Map<String, Map<String, String>>> getTotalEquipmentPerFormationDailyFailureData(
            @RequestParam(required = false) List<Long> equipmentIds) {
        Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>> failureIntensityData =
                equipmentPerFormationService.getTotalFailureIntensityData(tehoSession.getSessionId());
        return getTableDataDTO(null,
                               EquipmentPerFormation::getEquipment,
                               EquipmentPerFormationFailureIntensity::getAvgDailyFailure,
                               0.0,
                               va.rit.teho.controller.helper.Formatter::formatDouble,
                               failureIntensityData,
                               equipmentIds);
    }

    @GetMapping("/formation/{formationId}/equipment/daily-failure")
    @ResponseBody
    @ApiOperation(value = "Получить данные о ВВСТ в Формированиях c интенсивностью выхода в ремонт в ед. (в табличном виде)")
    public TableDataDTO<Map<String, Map<String, String>>> getEquipmentPerFormationDailyFailureData(
            @ApiParam(value = "Ключ ВЧ", required = true, example = "1") @PathVariable Long formationId,
            @RequestParam(required = false) List<Long> equipmentIds) {
        return this.getEquipmentRowData(formationId,
                                        EquipmentPerFormationFailureIntensity::getAvgDailyFailure,
                                        0.0,
                                        va.rit.teho.controller.helper.Formatter::formatDouble,
                                        equipmentIds);
    }

    private <K, T> EquipmentFailureIntensityRowData<String> getEquipmentFailureIntensityRowData(Map<K, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>> failureIntensityData,
                                                                                                Function<EquipmentPerFormation, K> keyGetter,
                                                                                                List<Stage> stages,
                                                                                                List<RepairType> repairTypes,
                                                                                                EquipmentPerFormation epb,
                                                                                                Function<EquipmentPerFormationFailureIntensity, T> getter,
                                                                                                T defaultValue,
                                                                                                Function<T, String> formatter) {
        Map<String, Map<String, String>> data = new HashMap<>();

        for (Stage s : stages) {
            for (RepairType rt : repairTypes) {
                EquipmentPerFormationFailureIntensity equipmentPerFormationFailureIntensity =
                        failureIntensityData
                                .getOrDefault(keyGetter.apply(epb), Collections.emptyMap())
                                .getOrDefault(rt, Collections.emptyMap())
                                .get(s);

                data.computeIfAbsent(s.getId().toString(), (e) -> new HashMap<>())
                    .put(rt.getId().toString(),
                         formatter
                                 .apply(equipmentPerFormationFailureIntensity == null ?
                                                defaultValue : getter.apply(equipmentPerFormationFailureIntensity) == null ?
                                         defaultValue : getter.apply(equipmentPerFormationFailureIntensity)));
            }
        }
        return new EquipmentFailureIntensityRowData<>(epb.getEquipment().getId(),
                                                      epb.getFormation() == null ? "" : epb
                                                              .getFormation()
                                                              .getShortName(),
                                                      epb.getEquipment().getName(),
                                                      epb.getAmount(),
                                                      data);
    }

}
