package va.rit.teho.controller.formation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.formation.FormationDTO;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.formation.FormationService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "formation", produces = MediaType.APPLICATION_JSON_VALUE)
@Transactional
@Api(tags = "Формирование (оно же Часть или Подразделение)")
public class FormationController {

    private final FormationService formationService;
    private final EquipmentService equipmentService;
    private final EquipmentPerFormationService equipmentPerFormationService;

    public FormationController(FormationService formationService,
                               EquipmentService equipmentService,
                               EquipmentPerFormationService equipmentPerFormationService) {
        this.formationService = formationService;
        this.equipmentService = equipmentService;
        this.equipmentPerFormationService = equipmentPerFormationService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ApiOperation(value = "Добавить Формирование")
    public ResponseEntity<FormationDTO> addFormation(@RequestBody FormationDTO formationModel) {
        Formation addedFormation = formationModel.getParentFormation() == null ?
                formationService.add(formationModel.getShortName(),
                                     formationModel.getFullName()) :
                formationService.add(formationModel.getShortName(),
                                     formationModel.getFullName(),
                                     formationModel.getParentFormation().getId());
        List<Long> equipment = equipmentService.list().stream().map(Equipment::getId).collect(Collectors.toList());
        equipmentPerFormationService.addEquipmentToFormation(addedFormation.getId(), equipment, 0);
        return ResponseEntity.status(HttpStatus.CREATED).body(FormationDTO.from(addedFormation, false));
    }

    @PutMapping(path = "/{formationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить Формирование")
    public ResponseEntity<FormationDTO> updateFormation(@PathVariable Long formationId,
                                                        @RequestBody FormationDTO formationModel) {
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
        return ResponseEntity.ok(formationService.list().stream().map(FormationDTO::from).collect(Collectors.toList()));
    }

    @GetMapping("/{formationId}")
    @ResponseBody
    @ApiOperation(value = "Получить подробности о Формировании")
    public ResponseEntity<FormationDTO> getFormation(@PathVariable Long formationId) {
        return ResponseEntity.ok(FormationDTO.from(formationService.get(formationId), true));
    }

}
