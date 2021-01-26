package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.common.IdAndNameDTO;
import va.rit.teho.entity.repairformation.RepairStationType;
import va.rit.teho.service.repairformation.RepairStationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping(path = "repair-station", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Мастерские")
public class RepairStationController {
    private final RepairStationService repairStationService;

    public RepairStationController(RepairStationService repairStationService) {
        this.repairStationService = repairStationService;
    }

    @GetMapping(path = "/type")
    @ResponseBody
    public ResponseEntity<List<IdAndNameDTO>> listRepairStationTypes() {
        return ResponseEntity.ok(repairStationService
                                         .listTypes()
                                         .stream()
                                         .map(repairStationType -> new IdAndNameDTO(
                                                 repairStationType.getId(),
                                                 repairStationType.getName()))
                                         .collect(Collectors.toList()));
    }

    @PostMapping(path = "/type")
    @ResponseBody
    public ResponseEntity<IdAndNameDTO> addRepairStationType(@Valid @RequestBody IdAndNameDTO idAndNameDTO) {
        RepairStationType rst = repairStationService.addType(idAndNameDTO.getName());
        return ResponseEntity.ok(new IdAndNameDTO(rst.getId(), rst.getName()));
    }

    @PutMapping(path = "/type/{typeId}")
    @ResponseBody
    public ResponseEntity<IdAndNameDTO> updateRepairStationType(
            @PathVariable @Positive Long typeId,
            @Valid @RequestBody IdAndNameDTO idAndNameDTO) {
        RepairStationType rst = repairStationService.updateType(typeId, idAndNameDTO.getName());
        return ResponseEntity.accepted().body(new IdAndNameDTO(rst.getId(), rst.getName()));
    }

    @DeleteMapping(path = "/type/{typeId}")
    public ResponseEntity<Object> deleteRepairStationType(
            @PathVariable @Positive Long typeId) {
        repairStationService.deleteType(typeId);
        return ResponseEntity.noContent().build();
    }
}
