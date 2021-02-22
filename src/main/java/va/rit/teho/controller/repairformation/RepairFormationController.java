package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.formation.FormationDTO;
import va.rit.teho.dto.repairformation.RepairFormationDTO;
import va.rit.teho.entity.repairformation.RepairFormation;
import va.rit.teho.service.repairformation.RepairFormationService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Ремонтные формирования")
public class RepairFormationController {

    private final RepairFormationService repairFormationService;

    public RepairFormationController(RepairFormationService repairFormationService) {
        this.repairFormationService = repairFormationService;
    }

    @GetMapping("/formation/{formationId}/repair-formation")
    @ResponseBody
    @ApiOperation(value = "Получить список Ремонтных Формирований, принадлежащих данному Формированию")
    public ResponseEntity<List<RepairFormationDTO>> listRepairFormationsInFormation(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable @Positive Long formationId) {
        return ResponseEntity.ok(repairFormationService
                                         .list(formationId)
                                         .stream()
                                         .map(rf -> RepairFormationDTO.from(rf, true))
                                         .collect(Collectors.toList()));
    }

    @GetMapping("/formation/repair-formation/grouped")
    @ResponseBody
    @ApiOperation(value = "Получить список всех Ремонтных Формирований, сгруппированый по Формированиям, которым они принадлежат")
    public ResponseEntity<List<FormationDTO>> listRepairFormationsGroupedByFormation() {
        List<FormationDTO> formationDTOList = repairFormationService
                .list()
                .stream()
                .collect(Collectors.groupingBy(RepairFormation::getFormation))
                .entrySet()
                .stream()
                .map(formationListEntry -> FormationDTO.from(formationListEntry.getKey(),
                                                             formationListEntry.getValue()))
                .sorted(Comparator.comparing(FormationDTO::getId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(formationDTOList);
    }


    @GetMapping("/formation/repair-formation")
    @ResponseBody
    @ApiOperation(value = "Получить список всех Ремонтных Формирований")
    public ResponseEntity<List<RepairFormationDTO>> listRepairFormations() {
        List<RepairFormationDTO> repairFormationDTOList = repairFormationService
                .list()
                .stream()
                .map(rf -> RepairFormationDTO.from(rf, true))
                .collect(Collectors.toList());

        return ResponseEntity.ok(repairFormationDTOList);
    }

    @GetMapping("/formation/repair-formation/{repairFormationId}")
    @ResponseBody
    @ApiOperation(value = "Получить детали Ремонтного Формирования (название, тип, Формирование)")
    public ResponseEntity<RepairFormationDTO> getRepairFormationDetails(@PathVariable @Positive Long repairFormationId) {
        return ResponseEntity.ok(RepairFormationDTO.from(repairFormationService.get(repairFormationId), true));
    }

    @PostMapping(path = "/formation/{formationId}/repair-formation", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавить Ремонтное Формирование (обязательно: имя, id типа, id формирования)")
    public ResponseEntity<RepairFormationDTO> addRepairFormation(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable @Positive Long formationId,
            @Valid @RequestBody RepairFormationDTO repairFormationDTO) {
        RepairFormation repairFormation = repairFormationService.add(repairFormationDTO.getName(),
                                                                     repairFormationDTO.getType().getId(),
                                                                     formationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(RepairFormationDTO.from(repairFormation, true));
    }

    @PutMapping(path = "/formation/{formationId}/repair-formation/{repairFormationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить Ремонтное Формирование")
    public ResponseEntity<RepairFormationDTO> updateRepairFormation(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable @Positive Long formationId,
            @ApiParam(value = "Ключ обновляемого ремонтного формирования", required = true) @PathVariable @Positive Long repairFormationId,
            @Valid @RequestBody RepairFormationDTO repairFormationDTO) {
        RepairFormation repairFormation = repairFormationService.update(repairFormationId,
                                                                        repairFormationDTO.getName(),
                                                                        repairFormationDTO.getType().getId(),
                                                                        formationId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(RepairFormationDTO.from(repairFormation, true));
    }

    @DeleteMapping(path = "/formation/{formationId}/repair-formation/{repairFormationId}")
    @Transactional
    @ApiOperation(value = "Удалить Ремонтное Формирование и все связанные сущности")
    public ResponseEntity<Object> deleteRepairFormation(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable @Positive Long formationId,
            @ApiParam(value = "Ключ обновляемого ремонтного формирования", required = true) @PathVariable @Positive Long repairFormationId) {
        if (!repairFormationService.get(repairFormationId).getRepairFormationUnitSet().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Удаление невозможно: у ремонтного формирования (id = " + repairFormationId + ") существуют РВО.");
        }
        repairFormationService.delete(repairFormationId);
        return ResponseEntity.noContent().build();
    }
}
