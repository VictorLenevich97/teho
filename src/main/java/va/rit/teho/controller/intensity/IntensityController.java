package va.rit.teho.controller.intensity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.controller.helper.ReportResponseEntity;
import va.rit.teho.dto.equipment.EquipmentFailureIntensityRowData;
import va.rit.teho.dto.table.GenericTableDataDTO;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.intensity.IntensityData;
import va.rit.teho.entity.intensity.combined.EquipmentIntensityCombinedData;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.common.StageService;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.intensity.IntensityService;
import va.rit.teho.service.report.ReportService;

import javax.validation.constraints.Positive;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Controller
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Интенсивность выхода из строя")
public class IntensityController {

    private final IntensityService intensityService;
    private final RepairTypeService repairTypeService;
    private final StageService stageService;
    private final EquipmentService equipmentService;
    private final ReportService<EquipmentIntensityCombinedData> reportService;

    public IntensityController(IntensityService intensityService, RepairTypeService repairTypeService, StageService stageService, EquipmentService equipmentService, ReportService<EquipmentIntensityCombinedData> reportService) {
        this.intensityService = intensityService;
        this.repairTypeService = repairTypeService;
        this.stageService = stageService;
        this.equipmentService = equipmentService;
        this.reportService = reportService;
    }

    @PutMapping(path = "/operation/{operationId}/intensity/{equipmentId}")
    @ApiOperation(value = "Обновить данные об интенсивности выхода ВВСТ в ремонт")
    public ResponseEntity<Object> updateEquipmentIntensityData(
            @ApiParam(value = "Ключ Операции", required = true, example = "1") @PathVariable @Positive Long operationId,
            @ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable @Positive Long equipmentId,
            @ApiParam(value = "Данные о выходе ВВСТ в ремонт (%) ({'ключ этапа': {'ключ типа ремонта': 'значение'}})", required = true, example = "{'1': {'1': '21', '2': '15'}}")
            @RequestBody Map<Long, Map<Long, Double>> data) {
        intensityService.setIntensities(operationId, equipmentId, data);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/operation/{operationId}/intensity")
    @ResponseBody
    @ApiOperation(value = "Получить данные о ВВСТ с интенсивностью выхода в ремонт в % (в табличном виде)")
    public GenericTableDataDTO<Map<String, Map<String, String>>, EquipmentFailureIntensityRowData<String>> getEquipmentIntensityData(
            @ApiParam(value = "Ключ Операции", required = true, example = "1")
            @PathVariable @Positive Long operationId) {
        List<Stage> stages = stageService.list();
        List<RepairType> repairTypes = repairTypeService.list(true);
        IntensityData intensitiesForOperation = intensityService.getIntensitiesForOperation(operationId);
        List<Equipment> equipmentList = equipmentService.list();

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

        List<EquipmentFailureIntensityRowData<String>> intensityRowData = new ArrayList<>();

        for (Equipment equipment : equipmentList) {
            Map<String, Map<String, String>> data = new HashMap<>();
            for (Stage s : stages) {
                for (RepairType rt : repairTypes) {
                    data.computeIfAbsent(s.getId().toString(), e -> new HashMap<>())
                            .put(rt.getId().toString(), intensitiesForOperation.get(equipment, rt, s).toString());
                }
            }
            intensityRowData.add(new EquipmentFailureIntensityRowData<>(equipment.getId(), equipment.getName(), data));
        }

        return new GenericTableDataDTO<>(stageColumns, intensityRowData);
    }

    @GetMapping("/operation/{operationId}/intensity/report")
    @ResponseBody
    @ApiOperation(value = "Получить отчет по интенсивности выхода ВВСТ в ремонт (%)")
    public ResponseEntity<byte[]> getEquipmentIntensityReport(
            @ApiParam(value = "Ключ Операции", required = true, example = "1")
            @PathVariable @Positive Long operationId) throws
            UnsupportedEncodingException {
        List<Stage> stages = stageService.list();
        List<RepairType> repairTypes = repairTypeService.list(true);
        IntensityData intensitiesForOperation = intensityService.getIntensitiesForOperation(operationId);
        List<Equipment> equipmentList = equipmentService.list();

        byte[] report = reportService.generateReport(new EquipmentIntensityCombinedData(stages, repairTypes, intensitiesForOperation, equipmentList));

        return ReportResponseEntity.ok("Интенсивность выхода ВВСТ в ремонт", report);
    }


}
