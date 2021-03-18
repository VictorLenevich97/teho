package va.rit.teho.controller.equipment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.controller.helper.Formatter;
import va.rit.teho.controller.helper.ReportResponseEntity;
import va.rit.teho.dto.equipment.EquipmentFailureIntensityRowData;
import va.rit.teho.dto.equipment.EquipmentPerFormationDTO;
import va.rit.teho.dto.equipment.IntensityAndAmountDTO;
import va.rit.teho.dto.table.GenericTableDataDTO;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensity;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.equipment.combined.EquipmentFailureIntensityCombinedData;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.common.StageService;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.report.ReportService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static va.rit.teho.controller.helper.FilterConverter.nullIfEmpty;

@Controller
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "ВВСТ в Формированиях")
public class EquipmentPerFormationController {

    private static final String REPORT_NAME = "Среднесуточный выход ВВСТ в ремонт";

    private final StageService stageService;
    private final RepairTypeService repairTypeService;
    private final EquipmentPerFormationService equipmentPerFormationService;
    private final ReportService<EquipmentFailureIntensityCombinedData> reportService;

    @Resource
    private TehoSessionData tehoSession;

    public EquipmentPerFormationController(StageService stageService,
                                           RepairTypeService repairTypeService,
                                           EquipmentPerFormationService equipmentPerFormationService,
                                           ReportService<EquipmentFailureIntensityCombinedData> reportService) {
        this.stageService = stageService;
        this.repairTypeService = repairTypeService;
        this.equipmentPerFormationService = equipmentPerFormationService;
        this.reportService = reportService;
    }


    @PostMapping(path = "/formation/{formationId}/equipment/{equipmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавить ВВСТ в Формирование")
    public ResponseEntity<Object> addEquipmentToFormation(@ApiParam(value = "Ключ ВЧ", required = true, example = "1") @Positive @PathVariable @Positive Long formationId,
                                                          @ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @Positive @PathVariable @Positive Long equipmentId,
                                                          @ApiParam(value = "Количество ВВСТ в Формировании", required = true) @Valid @RequestBody IntensityAndAmountDTO amount) {
        EquipmentPerFormation equipmentPerFormation = equipmentPerFormationService.addEquipmentToFormation(formationId,
                                                                                                           equipmentId,
                                                                                                           (long) amount
                                                                                                                   .getAmount());
        return ResponseEntity.ok().body(EquipmentPerFormationDTO.from(equipmentPerFormation));
    }

    @PutMapping(path = "/formation/{formationId}/equipment/{equipmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить ВВСТ в Формировании (количество)")
    public ResponseEntity<EquipmentPerFormationDTO> updateEquipmentInFormationAmount(@ApiParam(value = "Ключ ВЧ", required = true, example = "1") @PathVariable @Positive Long formationId,
                                                                                     @ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable @Positive Long equipmentId,
                                                                                     @ApiParam(value = "Количество ВВСТ в Формировании", required = true) @Valid @RequestBody IntensityAndAmountDTO amount) {
        EquipmentPerFormation equipmentPerFormation = equipmentPerFormationService.updateEquipmentInFormation(
                formationId,
                equipmentId,
                amount.getAmount());

        return ResponseEntity.accepted().body(EquipmentPerFormationDTO.from(equipmentPerFormation));
    }

    @DeleteMapping("/formation/{formationId}/equipment/{equipmentId}")
    @Transactional
    @ApiOperation(value = "Удалить ВВСТ из Формирования")
    public ResponseEntity<Object> deleteEquipmentFromFormation(@PathVariable @Positive Long formationId,
                                                               @PathVariable @Positive Long equipmentId) {
        equipmentPerFormationService.deleteEquipmentFromFormation(formationId, equipmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/formation/{formationId}/equipment")
    @ResponseBody
    @ApiOperation(value = "Получить список ВВСТ в Формированиях")
    public ResponseEntity<List<EquipmentPerFormationDTO>> getEquipmentPerFormationData(
            @ApiParam(value = "Ключ ВЧ", required = true, example = "1") @PathVariable @Positive Long formationId) {
        return ResponseEntity.ok(equipmentPerFormationService
                                         .getEquipmentInFormation(formationId, null)
                                         .stream()
                                         .map(EquipmentPerFormationDTO::from)
                                         .sorted(Comparator.comparing(EquipmentPerFormationDTO::getEquipmentId,
                                                                      Comparator.reverseOrder()))
                                         .collect(Collectors.toList()));
    }

    private GenericTableDataDTO<Map<String, Map<String, String>>, EquipmentFailureIntensityRowData<String>> getEquipmentRowData(Long formationId, List<Long> equipmentIds) {
        Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>> failureIntensityData =
                equipmentPerFormationService.getFailureIntensityData(tehoSession.getSessionId(),
                        formationId,
                        equipmentIds);
        return getTableDataDTO(formationId, EquipmentPerFormation::getEquipment, Collections.singletonMap(null, failureIntensityData), equipmentIds);
    }

    @GetMapping("/formation/equipment/daily-failure")
    @ResponseBody
    @ApiOperation(value = "Таблица с интенсивностью выхода ВВСТ в ремонт (по всем Формированиям), %")
    public GenericTableDataDTO<Map<String, Map<String, String>>, EquipmentFailureIntensityRowData<String>> getTotalEquipmentPerFormationDailyFailureData(
            @RequestParam(required = false) List<Long> equipmentIds) {
        Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> failureIntensityData =
                equipmentPerFormationService.getTotalFailureIntensityData(tehoSession.getSessionId());
        return getTableDataDTO(null,
                EquipmentPerFormation::getEquipment,
                failureIntensityData,
                equipmentIds);
    }

    @GetMapping("/formation/equipment/daily-failure/report")
    @ResponseBody
    @ApiOperation(value = "Получить очтет с интенсивностью выхода ВВСТ в ремонт (по всем Формированиям), %")
    public ResponseEntity<byte[]> getTotalEquipmentPerFormationDailyFailureDataReport(
            @RequestParam(required = false) List<Long> equipmentIds) throws UnsupportedEncodingException {
        Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> failureIntensityData =
                equipmentPerFormationService.getTotalFailureIntensityData(tehoSession.getSessionId());
        List<Stage> stages = stageService.list();
        List<RepairType> repairTypes = repairTypeService.list(true);
        Map<Formation, Map<EquipmentType, List<EquipmentPerFormation>>> totalEquipmentInFormations =
                equipmentPerFormationService.getTotalGroupedEquipmentInFormations(equipmentIds);
        byte[] bytes = reportService.generateReport(new EquipmentFailureIntensityCombinedData(stages,
                                                                                              repairTypes,
                                                                                              totalEquipmentInFormations,
                                                                                              failureIntensityData,
                                                                                              EquipmentPerFormationFailureIntensity::getAvgDailyFailure,
                                                                                              "ед."));

        return ReportResponseEntity.ok(REPORT_NAME, bytes);
    }

    @GetMapping("/formation/{formationId}/equipment/daily-failure/report")
    @ResponseBody
    @ApiOperation(value = "Получить данные о ВВСТ в Формированиях c интенсивностью выхода в ремонт в ед. (в виде Excel-отчета)")
    public ResponseEntity<byte[]> getEquipmentPerFormationDailyFailureDataReport(
            @ApiParam(value = "Ключ ВЧ", required = true, example = "1") @PathVariable @Positive Long formationId,
            @RequestParam(required = false) List<Long> equipmentIds) throws UnsupportedEncodingException {
        byte[] bytes = generateEquipmentFailureReport(formationId,
                nullIfEmpty(equipmentIds),
                EquipmentPerFormationFailureIntensity::getAvgDailyFailure
        );

        return ReportResponseEntity.ok(REPORT_NAME, bytes);
    }

    private byte[] generateEquipmentFailureReport(Long formationId,
                                                  List<Long> equipmentIds,
                                                  Function<EquipmentPerFormationFailureIntensity, Number> intensityFunction) {
        Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>> failureIntensityData =
                equipmentPerFormationService.getFailureIntensityData(tehoSession.getSessionId(),
                        formationId,
                        equipmentIds);
        List<Stage> stages = stageService.list();
        List<RepairType> repairTypes = repairTypeService.list(true);
        Map<EquipmentType, List<EquipmentPerFormation>> totalEquipmentInFormations =
                equipmentPerFormationService.getGroupedEquipmentInFormation(formationId, equipmentIds);
        return reportService.generateReport(
                new EquipmentFailureIntensityCombinedData(stages,
                        repairTypes,
                        Collections.singletonMap(null, totalEquipmentInFormations),
                        Collections.singletonMap(null, failureIntensityData),
                        intensityFunction,
                        "ед."));
    }

    @GetMapping("/formation/{formationId}/equipment/daily-failure")
    @ResponseBody
    @ApiOperation(value = "Получить данные о ВВСТ в Формированиях c интенсивностью выхода в ремонт в ед. (в табличном виде)")
    public ResponseEntity<GenericTableDataDTO<Map<String, Map<String, String>>, EquipmentFailureIntensityRowData<String>>> getEquipmentPerFormationDailyFailureData(
            @ApiParam(value = "Ключ ВЧ", required = true, example = "1") @PathVariable @Positive Long formationId,
            @RequestParam(required = false) List<Long> equipmentIds) {
        return ResponseEntity.ok(this.getEquipmentRowData(formationId, nullIfEmpty(equipmentIds)));
    }

    @PostMapping("/formation/{formationId}/equipment/daily-failure/{coefficient}")
    @ApiOperation(value = "Обновить данные о выходе ВВСТ из строя на основе интенсивности (%)")
    public ResponseEntity<GenericTableDataDTO<Map<String, Map<String, String>>, EquipmentFailureIntensityRowData<String>>> updateAvgDailyFailureData(
            @ApiParam(value = "Ключ формирования", required = true, example = "1") @PathVariable @Positive Long formationId,
            @ApiParam(value = "Коэффициент (k), используемый в расчетах", required = true) @PathVariable Double coefficient) {

        equipmentPerFormationService.calculateAndSetEquipmentPerFormationDailyFailure(tehoSession.getSessionId(),
                formationId,
                coefficient);
        return getEquipmentPerFormationDailyFailureData(formationId, Collections.emptyList());
    }

    @PutMapping("/formation/{formationId}/equipment/{equipmentId}/daily-failure")
    @ResponseBody
    @ApiOperation(value = "Обновить данные о выходе ВВСТ в ремонт в ед.")
    public ResponseEntity<Object> updateEquipmentPerFormationFailureData(
            @ApiParam(value = "Ключ формирования", required = true, example = "1") @PathVariable @Positive Long formationId,
            @ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable @Positive Long equipmentId,
            @ApiParam(value = "Данные о выходе ВВСТ в ремонт ({'ключ этапа': {'ключ типа ремонта': 'значение'}})",
                    required = true,
                    example = "{'1': {'1': '1.2', '2': '4.3'}}") @RequestBody Map<Long, Map<Long, Double>> data) {
        data.forEach((stageId, repairTypeIntensityMap) ->
                             repairTypeIntensityMap
                                     .forEach((repairTypeId, dailyFailure) ->
                                                      equipmentPerFormationService
                                                              .setEquipmentPerFormationDailyFailure(
                                                                      tehoSession.getSessionId(),
                                                                      formationId,
                                                                      equipmentId,
                                                                      repairTypeId,
                                                                      stageId,
                                                                      dailyFailure)));
        return ResponseEntity.accepted().build();
    }


    private <K> GenericTableDataDTO<Map<String, Map<String, String>>, EquipmentFailureIntensityRowData<String>> getTableDataDTO(
            Long formationId,
            Function<EquipmentPerFormation, K> keyGetter,
            Map<Formation, Map<K, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> failureIntensityData,
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
            NestedColumnsDTO nestedColumnsDTO = new NestedColumnsDTO(s.getStageNum().toString(), repairTypeColumns);
            stageColumns.add(nestedColumnsDTO);
        }

        List<EquipmentPerFormation> equipmentPerFormations = formationId == null ?
                equipmentPerFormationService.getEquipmentInAllFormations(equipmentIds) :
                equipmentPerFormationService.getEquipmentInFormation(formationId, equipmentIds);

        List<EquipmentFailureIntensityRowData<String>> rowData =
                equipmentPerFormations
                        .stream()
                        .map(epb -> getEquipmentFailureIntensityRowData(failureIntensityData,
                                keyGetter,
                                stages,
                                repairTypes,
                                epb))
                        .sorted(Comparator.comparing(EquipmentFailureIntensityRowData::getId,
                                                     Comparator.reverseOrder()))
                        .collect(Collectors.toList());
        return new GenericTableDataDTO<>(stageColumns, rowData);
    }

    private <K> EquipmentFailureIntensityRowData<String> getEquipmentFailureIntensityRowData(Map<Formation, Map<K, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> failureIntensityData,
                                                                                                Function<EquipmentPerFormation, K> keyGetter,
                                                                                                List<Stage> stages,
                                                                                                List<RepairType> repairTypes,
                                                                                                EquipmentPerFormation epb) {
        Map<String, Map<String, String>> data = new HashMap<>();

        for (Stage s : stages) {
            for (RepairType rt : repairTypes) {
                EquipmentPerFormationFailureIntensity equipmentPerFormationFailureIntensity =
                        failureIntensityData
                                .getOrDefault(epb.getFormation(),
                                        failureIntensityData
                                                .values()
                                                .stream()
                                                .findFirst()
                                                .orElse(Collections.emptyMap()))
                                .getOrDefault(keyGetter.apply(epb), Collections.emptyMap())
                                .getOrDefault(rt, Collections.emptyMap())
                                .get(s);

                data.computeIfAbsent(s.getId().toString(), e -> new HashMap<>())
                        .put(rt.getId().toString(),
                                Formatter.formatDoubleAsString(Optional
                                        .ofNullable(equipmentPerFormationFailureIntensity)
                                        .map(EquipmentPerFormationFailureIntensity::getAvgDailyFailure)
                                        .orElse(0.0)));
            }
        }
        return new EquipmentFailureIntensityRowData<>(epb.getEquipment().getId(),
                                                      Optional
                                                              .ofNullable(epb.getFormation())
                                                              .map(Formation::getShortName)
                                                              .orElse(""),
                                                      epb.getEquipment().getName(),
                                                      epb.getAmount(),
                                                      data);
    }

}
