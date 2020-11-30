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
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.equipment.EquipmentService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "formation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Transactional
@Api(tags = "Часть/Подразделение")
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

    @PostMapping
    @Transactional
    @ApiOperation(value = "Добавить Часть/Подразделение")
    public ResponseEntity<Object> addFormation(@RequestBody FormationDTO formationModel) {
        Long formationId = formationService.add(formationModel.getShortName(), formationModel.getFullName());
        List<Long> equipment = equipmentService.list().stream().map(Equipment::getId).collect(Collectors.toList());
        equipmentPerFormationService.addEquipmentToFormation(formationId, equipment, 0);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{formationId}")
    @ApiOperation(value = "Обновить Часть/Подразделение")
    public ResponseEntity<Object> updateFormation(@PathVariable Long formationId, @RequestBody FormationDTO formationModel) {
        formationService.update(formationId, formationModel.getShortName(), formationModel.getFullName());
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(value = "Получить список Часть/Подразделение")
    public ResponseEntity<List<FormationDTO>> listFormations() {
        return ResponseEntity.ok(formationService.list().stream().map(FormationDTO::from).collect(Collectors.toList()));
    }

    @GetMapping("/{formationId}")
    @ResponseBody
    @ApiOperation(value = "Получить подробности о Часть/Подразделение")
    public ResponseEntity<FormationDTO> getFormation(@PathVariable Long formationId) {
        return ResponseEntity.ok(FormationDTO.from(formationService.get(formationId)));
    }

}
