package va.rit.teho.controller.equipment;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.common.StageService;
import va.rit.teho.service.equipment.EquipmentPerBaseService;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class EquipmentPerBaseController {

    private final StageService stageService;
    private final RepairTypeService repairTypeService;
    private final EquipmentPerBaseService equipmentPerBaseService;

    @Resource
    private TehoSessionData tehoSession;

    public EquipmentPerBaseController(StageService stageService,
                                      RepairTypeService repairTypeService,
                                      EquipmentPerBaseService equipmentPerBaseService) {
        this.stageService = stageService;
        this.repairTypeService = repairTypeService;
        this.equipmentPerBaseService = equipmentPerBaseService;
    }


    @PostMapping("/base/{baseId}/equipment/{equipmentId}")
    public ResponseEntity<Object> addEquipmentToBase(@PathVariable Long baseId,
                                                     @PathVariable Long equipmentId,
                                                     @RequestBody IntensityAndAmountDTO intensityAndAmount) {
        equipmentPerBaseService.addEquipmentToBase(baseId,
                                                   equipmentId,
                                                   intensityAndAmount.getAmount());
        intensityAndAmount
                .getIntensity()
                .forEach(intensityPerRepairTypeAndStageDTO ->
                                 equipmentPerBaseService.setEquipmentPerBaseFailureIntensity(
                                         tehoSession.getSessionId(),
                                         baseId,
                                         equipmentId,
                                         intensityPerRepairTypeAndStageDTO
                                                 .getRepairTypeId(),
                                         intensityPerRepairTypeAndStageDTO
                                                 .getStageId(),
                                         intensityPerRepairTypeAndStageDTO
                                                 .getIntensity()));
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/base/{baseId}/equipment/{equipmentId}")
    public ResponseEntity<Object> updateEquipmentInBase(@PathVariable Long baseId,
                                                        @PathVariable Long equipmentId,
                                                        @RequestBody IntensityAndAmountDTO intensityAndAmount) {
        equipmentPerBaseService.updateEquipmentInBase(baseId,
                                                      equipmentId,
                                                      intensityAndAmount.getAmount());
        intensityAndAmount
                .getIntensity()
                .forEach(intensityPerRepairTypeAndStageDTO ->
                                 equipmentPerBaseService.setEquipmentPerBaseFailureIntensity(
                                         tehoSession.getSessionId(), baseId,
                                         equipmentId,
                                         intensityPerRepairTypeAndStageDTO.getRepairTypeId(),
                                         intensityPerRepairTypeAndStageDTO.getStageId(),
                                         intensityPerRepairTypeAndStageDTO.getIntensity()));
        return ResponseEntity.accepted().build();
    }


    @GetMapping("/base/{baseId}/equipment")
    @ResponseBody
    public TableDataDTO<Map<String, Map<String, Integer>>> getEquipmentPerBaseData(@PathVariable Long baseId) {
        return this.getEquipmentRowData(baseId, EquipmentPerBaseFailureIntensity::getIntensityPercentage, 0);
    }

    private <T> TableDataDTO<Map<String, Map<String, T>>> getEquipmentRowData(Long baseId,
                                                                              Function<EquipmentPerBaseFailureIntensity, T> getter,
                                                                              T defaultValue) {
        Map<Pair<Base, Equipment>, Map<RepairType, Map<Stage, EquipmentPerBaseFailureIntensity>>> failureIntensityData =
                equipmentPerBaseService.getFailureIntensityData(tehoSession.getSessionId(), baseId);
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

        List<EquipmentFailureIntensityRowData<T>> rowData =
                equipmentPerBaseService.getEquipmentInBases()
                                       .stream()
                                       .map(epb -> getEquipmentFailureIntensityRowData(failureIntensityData,
                                                                                       stages,
                                                                                       repairTypes,
                                                                                       epb,
                                                                                       getter,
                                                                                       defaultValue))
                                       .collect(Collectors.toList());
        return new TableDataDTO<>(stageColumns, rowData);
    }

    @GetMapping("/base/{baseId}/equipment/daily-failure")
    @ResponseBody
    public TableDataDTO<Map<String, Map<String, Double>>> getEquipmentPerBaseDailyFailureData(@PathVariable Long baseId) {
        return this.getEquipmentRowData(baseId, EquipmentPerBaseFailureIntensity::getAvgDailyFailure, 0.0);
    }

    private <T> EquipmentFailureIntensityRowData<T> getEquipmentFailureIntensityRowData(Map<Pair<Base, Equipment>, Map<RepairType, Map<Stage, EquipmentPerBaseFailureIntensity>>> failureIntensityData,
                                                                                        List<Stage> stages,
                                                                                        List<RepairType> repairTypes,
                                                                                        EquipmentPerBase epb,
                                                                                        Function<EquipmentPerBaseFailureIntensity, T> getter,
                                                                                        T defaultValue) {
        Map<String, Map<String, T>> data = new HashMap<>();

        for (Stage s : stages) {
            for (RepairType rt : repairTypes) {
                EquipmentPerBaseFailureIntensity equipmentPerBaseFailureIntensity =
                        failureIntensityData
                                .getOrDefault(Pair.of(epb.getBase(), epb.getEquipment()), Collections.emptyMap())
                                .getOrDefault(rt, Collections.emptyMap())
                                .get(s);

                data.computeIfAbsent(s.getId().toString(), (e) -> new HashMap<>())
                    .put(rt.getId().toString(),
                         equipmentPerBaseFailureIntensity == null ? defaultValue : getter.apply(
                                 equipmentPerBaseFailureIntensity) == null ? defaultValue : getter.apply(
                                 equipmentPerBaseFailureIntensity));
            }
        }
        return new EquipmentFailureIntensityRowData<>(epb.getBase().getShortName(),
                                                      epb.getEquipment().getName(),
                                                      epb.getAmount(),
                                                      data);
    }

}
