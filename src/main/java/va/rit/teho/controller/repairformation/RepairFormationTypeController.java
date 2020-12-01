package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.repairformation.RepairFormationTypeDTO;
import va.rit.teho.service.repairformation.RepairFormationUnitTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "repair-formation-type", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Типы РВО")
public class RepairFormationTypeController {

    private final RepairFormationUnitTypeService repairFormationUnitTypeService;

    public RepairFormationTypeController(RepairFormationUnitTypeService repairFormationUnitTypeService) {
        this.repairFormationUnitTypeService = repairFormationUnitTypeService;
    }

    @GetMapping
    @ApiOperation(value = "Получение списка типов РВО")
    public ResponseEntity<List<RepairFormationTypeDTO>> listRepairStationTypes() {
        List<RepairFormationTypeDTO> repairFormationTypeDTOList = repairFormationUnitTypeService.listTypes()
                                                                                                .stream()
                                                                                                .map(RepairFormationTypeDTO::from)
                                                                                                .collect(Collectors.toList());
        return ResponseEntity.ok(repairFormationTypeDTOList);
    }

    @PostMapping
    @ApiOperation(value = "Добавление типа РВО")
    public ResponseEntity<Object> addRepairStationType(@ApiParam(value = "Данные о типе РВО", required = true) @RequestBody RepairFormationTypeDTO repairFormationTypeDTO) {
        repairFormationUnitTypeService.addType(repairFormationTypeDTO.getName(),
                                               repairFormationTypeDTO.getWorkingHoursMin(),
                                               repairFormationTypeDTO.getWorkingHoursMax());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{typeId}")
    @ApiOperation(value = "Обновление типа РВО")
    public ResponseEntity<Object> updateRepairStationType(@ApiParam(value = "Ключ типа РВО", required = true) @PathVariable Long typeId,
                                                          @ApiParam(value = "Данные о типе РВО", required = true) @RequestBody RepairFormationTypeDTO repairFormationTypeDTO) {
        repairFormationUnitTypeService.updateType(typeId,
                                                  repairFormationTypeDTO.getName(),
                                                  repairFormationTypeDTO.getWorkingHoursMin(),
                                                  repairFormationTypeDTO.getWorkingHoursMax());
        return ResponseEntity.accepted().build();
    }

}
