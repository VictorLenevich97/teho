package va.rit.teho.controller.formation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.formation.FormationDTO;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.repairformation.RepairFormationService;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping(path = "formation", produces = MediaType.APPLICATION_JSON_VALUE)
@Transactional
@Api(tags = "Формирование (оно же Часть или Подразделение)")
public class FormationController {

    private final FormationService formationService;
    private final EquipmentService equipmentService;

    @Resource
    private TehoSessionData tehoSession;

    private final RepairFormationService repairFormationService;
    private final EquipmentPerFormationService equipmentPerFormationService;

    public FormationController(FormationService formationService,
                               EquipmentService equipmentService,
                               RepairFormationService repairFormationService,
                               EquipmentPerFormationService equipmentPerFormationService) {
        this.formationService = formationService;
        this.equipmentService = equipmentService;
        this.repairFormationService = repairFormationService;
        this.equipmentPerFormationService = equipmentPerFormationService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ApiOperation(value = "Добавить Формирование")
    public ResponseEntity<FormationDTO> addFormation(@Valid @RequestBody FormationDTO formationModel) {
        Formation addedFormation = formationModel.getParentFormation() == null ?
                formationService.add(tehoSession.getSession(),
                        formationModel.getShortName(),
                        formationModel.getFullName()) :
                formationService.add(tehoSession.getSession(),
                        formationModel.getShortName(),
                        formationModel.getFullName(),
                        formationModel.getParentFormation().getId());
        List<Long> equipment = equipmentService.list().stream().map(Equipment::getId).collect(Collectors.toList());
        equipmentPerFormationService.addEquipmentToFormation(addedFormation.getId(), equipment, 0L);
        return ResponseEntity.status(HttpStatus.CREATED).body(FormationDTO.from(addedFormation, false));
    }

    @PutMapping(path = "/{formationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить Формирование")
    public ResponseEntity<FormationDTO> updateFormation(@PathVariable @Positive Long formationId,
                                                        @Valid @RequestBody FormationDTO formationModel) {
        Formation formation;
        if (formationModel.getParentFormation() == null) {
            formation = formationService.update(formationId,
                    formationModel.getShortName(),
                    formationModel.getFullName());
        } else {
            formation = formationService.update(formationId,
                    formationModel.getShortName(),
                    formationModel.getFullName(),
                    formationModel.getParentFormation().getId());
        }
        return ResponseEntity.accepted().body(FormationDTO.from(formation));
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(value = "Получить список Формирований")
    public ResponseEntity<List<FormationDTO>> listFormations() {
        return ResponseEntity.ok(formationService.list(tehoSession.getSessionId()).stream().map(FormationDTO::from).collect(Collectors.toList()));
    }

    @GetMapping("/{formationId}")
    @ResponseBody
    @ApiOperation(value = "Получить подробности о Формировании")
    public ResponseEntity<FormationDTO> getFormation(@PathVariable @Positive Long formationId) {
        return ResponseEntity.ok(FormationDTO.from(formationService.get(formationId), true));
    }

    @DeleteMapping("/{formationId}")
    @Transactional
    @ApiOperation(value = "Удаление формирования и всех связанных сущностей")
    public ResponseEntity<Object> deleteFormation(@PathVariable @Positive Long formationId) {
        if (!formationService.get(formationId).getChildFormations().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Удаление невозможно: у формирования (id = " + formationId + ") существуют дочерние формирования.");
        }

        if (!repairFormationService.list(formationId).isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Удаление невозможно: у формирования (id = " + formationId + ") существуют ремонтные формирования.");
        }
        formationService.delete(formationId);
        return ResponseEntity.noContent().build();
    }


}
