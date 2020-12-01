package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import va.rit.teho.dto.common.IdAndNameDTO;
import va.rit.teho.service.repairformation.RepairStationService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "repair-station", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Мастерские")
public class RepairStationController {
    private final RepairStationService repairStationService;

    public RepairStationController(RepairStationService repairStationService) {
        this.repairStationService = repairStationService;
    }

    @GetMapping(path = "/type", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<IdAndNameDTO>> listRepairStationTypes() {
        return ResponseEntity.ok(repairStationService
                                         .listRepairStationTypes()
                                         .stream()
                                         .map(repairStationType -> new IdAndNameDTO(
                                                 repairStationType.getId(),
                                                 repairStationType.getName()))
                                         .collect(Collectors.toList()));
    }
}
