package va.rit.teho.controller.equipment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import va.rit.teho.dto.equipment.EquipmentFailureIntensityRowData;
import va.rit.teho.dto.equipment.IntensityAndAmountDTO;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.base.Base;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentPerBase;
import va.rit.teho.entity.equipment.EquipmentPerBaseFailureIntensity;
import va.rit.teho.repository.equipment.EquipmentPerBaseFailureIntensityRepository;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.DataExporter;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.common.StageService;
import va.rit.teho.service.equipment.EquipmentPerBaseService;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "ВВСТ по ВЧ")
public class EquipmentPerBaseController {

    private final StageService stageService;
    private final RepairTypeService repairTypeService;
    private final EquipmentPerBaseService equipmentPerBaseService;
    private final EquipmentPerBaseFailureIntensityRepository equipmentPerBaseFailureIntensityRepository;

    @Resource
    private TehoSessionData tehoSession;

    public EquipmentPerBaseController(StageService stageService,
                                      RepairTypeService repairTypeService,
                                      EquipmentPerBaseService equipmentPerBaseService,
                                      EquipmentPerBaseFailureIntensityRepository equipmentPerBaseFailureIntensityRepository) {
        this.stageService = stageService;
        this.repairTypeService = repairTypeService;
        this.equipmentPerBaseService = equipmentPerBaseService;
        this.equipmentPerBaseFailureIntensityRepository = equipmentPerBaseFailureIntensityRepository;
    }


    @PostMapping("/base/{baseId}/equipment/{equipmentId}")
    @ApiOperation(value = "Добавить ВВСТ в ВЧ")
    public ResponseEntity<Object> addEquipmentToBase(@ApiParam(value = "Ключ ВЧ", required = true, example = "1") @PathVariable Long baseId,
                                                     @ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable Long equipmentId,
                                                     @ApiParam(value = "Количество ВВСТ в ВЧ и интенсивность выхода в ремонт", required = true) @RequestBody IntensityAndAmountDTO intensityAndAmount) {
        equipmentPerBaseService.addEquipmentToBase(baseId,
                                                   equipmentId,
                                                   (long) intensityAndAmount.getAmount());
        intensityAndAmount
                .getIntensity()
                .forEach(intensityPerRepairTypeAndStageDTO ->
                                 equipmentPerBaseService.setEquipmentPerBaseFailureIntensity(
                                         tehoSession.getSessionId(),
                                         baseId,
                                         equipmentId,
                                         intensityPerRepairTypeAndStageDTO.getRepairTypeId(),
                                         intensityPerRepairTypeAndStageDTO.getStageId(),
                                         intensityPerRepairTypeAndStageDTO.getIntensity()));
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/base/{baseId}/equipment/{equipmentId}")
    @ApiOperation(value = "Обновить ВВСТ в ВЧ")
    public ResponseEntity<Object> updateEquipmentInBase(@ApiParam(value = "Ключ ВЧ", required = true, example = "1") @PathVariable Long baseId,
                                                        @ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable Long equipmentId,
                                                        @ApiParam(value = "Количество ВВСТ в ВЧ и интенсивность выхода в ремонт", required = true) @RequestBody Map<Long, Map<Long, Integer>> intensityData) {
        //TODO: вынести в отдельный endpoint (?)
//        equipmentPerBaseService.updateEquipmentInBase(baseId,
//                                                      equipmentId,
//                                                      intensityAndAmount.getAmount());
        intensityData.forEach((stageId, repairTypeIntensityMap) ->
                                      repairTypeIntensityMap.forEach((repairTypeId, intensity) ->
                                                                             equipmentPerBaseService.setEquipmentPerBaseFailureIntensity(
                                                                                     tehoSession.getSessionId(),
                                                                                     baseId,
                                                                                     equipmentId,
                                                                                     repairTypeId,
                                                                                     stageId,
                                                                                     intensity)));
        return ResponseEntity.accepted().build();
    }


    @GetMapping("/base/{baseId}/equipment")
    @ResponseBody
    @ApiOperation(value = "Получить данные о ВВСТ в ВЧ (в табличном виде)")
    public TableDataDTO<Map<String, Map<String, String>>> getEquipmentPerBaseData(@ApiParam(value = "Ключ ВЧ", required = true, example = "1") @PathVariable Long baseId) {
        return this.getEquipmentRowData(baseId,
                                        EquipmentPerBaseFailureIntensity::getIntensityPercentage,
                                        0,
                                        Object::toString);
    }

    private <T> TableDataDTO<Map<String, Map<String, String>>> getEquipmentRowData(Long baseId,
                                                                                   Function<EquipmentPerBaseFailureIntensity, T> getter,
                                                                                   T defaultValue,
                                                                                   Function<T, String> formatter) {
        Map<Pair<Base, Equipment>, Map<RepairType, Map<Stage, EquipmentPerBaseFailureIntensity>>> failureIntensityData =
                equipmentPerBaseService.getFailureIntensityData(tehoSession.getSessionId(), baseId);
        return getTableDataDTO(baseId,
                               (epb) -> Pair.of(epb.getBase(), epb.getEquipment()),
                               getter,
                               defaultValue,
                               formatter,
                               failureIntensityData);
    }

    private <K, T> TableDataDTO<Map<String, Map<String, String>>> getTableDataDTO(Long baseId,
                                                                                  Function<EquipmentPerBase, K> keyGetter,
                                                                                  Function<EquipmentPerBaseFailureIntensity, T> getter,
                                                                                  T defaultValue,
                                                                                  Function<T, String> formatter,
                                                                                  Map<K, Map<RepairType, Map<Stage, EquipmentPerBaseFailureIntensity>>> failureIntensityData) {
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
                (baseId == null ? equipmentPerBaseService.getTotalEquipmentInBase() :
                        equipmentPerBaseService.getEquipmentInBase(baseId))
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

    @GetMapping("/base/equipment/daily-failure")
    @ResponseBody
    public TableDataDTO<Map<String, Map<String, String>>> getTotalEquipmentPerBaseDailyFailureData() {
        Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerBaseFailureIntensity>>> failureIntensityData =
                equipmentPerBaseService.getTotalFailureIntensityData(tehoSession.getSessionId());
        return getTableDataDTO(null,
                               EquipmentPerBase::getEquipment,
                               EquipmentPerBaseFailureIntensity::getAvgDailyFailure,
                               0.0,
                               va.rit.teho.controller.helper.Formatter::formatDouble,
                               failureIntensityData);
    }

    @GetMapping("/base/{baseId}/equipment/daily-failure")
    @ResponseBody
    @ApiOperation(value = "Получить данные о ВВСТ в ВЧ c интенсивностью выхода в ремонт в ед. (в табличном виде)")
    public TableDataDTO<Map<String, Map<String, String>>> getEquipmentPerBaseDailyFailureData(@ApiParam(value = "Ключ ВЧ", required = true, example = "1") @PathVariable Long baseId) {
        return this.getEquipmentRowData(baseId,
                                        EquipmentPerBaseFailureIntensity::getAvgDailyFailure,
                                        0.0,
                                        va.rit.teho.controller.helper.Formatter::formatDouble);
    }

    private <K, T> EquipmentFailureIntensityRowData<String> getEquipmentFailureIntensityRowData(Map<K, Map<RepairType, Map<Stage, EquipmentPerBaseFailureIntensity>>> failureIntensityData,
                                                                                                Function<EquipmentPerBase, K> keyGetter,
                                                                                                List<Stage> stages,
                                                                                                List<RepairType> repairTypes,
                                                                                                EquipmentPerBase epb,
                                                                                                Function<EquipmentPerBaseFailureIntensity, T> getter,
                                                                                                T defaultValue,
                                                                                                Function<T, String> formatter) {
        Map<String, Map<String, String>> data = new HashMap<>();

        for (Stage s : stages) {
            for (RepairType rt : repairTypes) {
                EquipmentPerBaseFailureIntensity equipmentPerBaseFailureIntensity =
                        failureIntensityData
                                .getOrDefault(keyGetter.apply(epb), Collections.emptyMap())
                                .getOrDefault(rt, Collections.emptyMap())
                                .get(s);

                data.computeIfAbsent(s.getId().toString(), (e) -> new HashMap<>())
                    .put(rt.getId().toString(),
                         formatter
                                    .apply(equipmentPerBaseFailureIntensity == null ?
                                                defaultValue : getter.apply(equipmentPerBaseFailureIntensity) == null ?
                                         defaultValue : getter.apply(equipmentPerBaseFailureIntensity)));
            }
        }
        return new EquipmentFailureIntensityRowData<>(epb.getEquipment().getId(),
                                                      epb.getBase() == null ? "" : epb.getBase().getShortName(),
                                                      epb.getEquipment().getName(),
                                                      epb.getAmount(),
                                                      data);
    }

}
