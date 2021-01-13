package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.formation.FormationDTO;
import va.rit.teho.dto.repairformation.RepairFormationDTO;
import va.rit.teho.entity.repairformation.RepairFormation;
import va.rit.teho.service.repairformation.RepairFormationService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Ремонтные формирования")
public class RepairFormationController {

    private final RepairFormationService repairFormationService;

    public RepairFormationController(RepairFormationService repairFormationService) {
        this.repairFormationService = repairFormationService;
    }

    @GetMapping("/formation/{formationId}/repair-formation")
    @ResponseBody
    public ResponseEntity<List<RepairFormationDTO>> listRepairFormationsInFormation(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable Long formationId) {
        return ResponseEntity.ok(repairFormationService
                                         .list(formationId)
                                         .stream()
                                         .map(rf -> RepairFormationDTO.from(rf, true))
                                         .collect(Collectors.toList()));
    }

    @GetMapping("/formation/repair-formation")
    @ResponseBody
    public ResponseEntity<List<FormationDTO>> listRepairFormations() {
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

    @GetMapping("/formation/repair-formation/{repairFormationId}")
    @ResponseBody
    public ResponseEntity<RepairFormationDTO> getRepairFormationDetails(@PathVariable Long repairFormationId) {
        return ResponseEntity.ok(RepairFormationDTO.from(repairFormationService.get(repairFormationId), true));
    }

    @PostMapping(path = "/formation/{formationId}/repair-formation", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RepairFormationDTO> addRepairFormation(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable Long formationId,
            @RequestBody RepairFormationDTO repairFormationDTO) {
        RepairFormation repairFormation = repairFormationService.add(repairFormationDTO.getName(),
                                                                     repairFormationDTO.getType().getId(),
                                                                     formationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(RepairFormationDTO.from(repairFormation, true));
    }

    @PutMapping(path = "/formation/{formationId}/repair-formation/{repairFormationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RepairFormationDTO> updateRepairFormation(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable Long formationId,
            @ApiParam(value = "Ключ обновляемого ремонтного формирования", required = true) @PathVariable Long repairFormationId,
            @RequestBody RepairFormationDTO repairFormationDTO) {
        RepairFormation repairFormation = repairFormationService.update(repairFormationId,
                                                                        repairFormationDTO.getName(),
                                                                        repairFormationDTO.getType().getId(),
                                                                        formationId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(RepairFormationDTO.from(repairFormation, true));
    }
}
