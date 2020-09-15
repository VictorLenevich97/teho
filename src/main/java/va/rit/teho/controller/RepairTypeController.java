package va.rit.teho.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import va.rit.teho.dto.RepairTypeDTO;
import va.rit.teho.entity.RepairType;
import va.rit.teho.service.RepairTypeService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("repair-type")
public class RepairTypeController {

    private final RepairTypeService repairTypeService;


    public RepairTypeController(RepairTypeService repairTypeService) {
        this.repairTypeService = repairTypeService;
    }

    @GetMapping
    public ResponseEntity<List<RepairTypeDTO>> listRepairTypes(@RequestParam(required = false) Boolean repairable) {
        List<RepairType> types;
        if (repairable == null) {
            types = repairTypeService.list();
        } else {
            types = repairTypeService.list(repairable);
        }
        return ResponseEntity.ok(types
                                         .stream()
                                         .map(RepairTypeDTO::from)
                                         .sorted(Comparator.comparing(RepairTypeDTO::getId))
                                         .collect(Collectors.toList()));
    }
}
