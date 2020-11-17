package va.rit.teho.controller.repairdivision;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.repairdivision.RepairDivisionUnitTypeDTO;
import va.rit.teho.service.repairdivision.RepairDivisionUnitTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "repair-division-type", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Типы РВО")
public class RepairDivisionUnitTypeController {

    private final RepairDivisionUnitTypeService repairDivisionUnitTypeService;

    public RepairDivisionUnitTypeController(RepairDivisionUnitTypeService repairDivisionUnitTypeService) {
        this.repairDivisionUnitTypeService = repairDivisionUnitTypeService;
    }

    @GetMapping
    @ApiOperation(value = "Получение списка типов РВО")
    public ResponseEntity<List<RepairDivisionUnitTypeDTO>> listRepairStationTypes() {
        List<RepairDivisionUnitTypeDTO> repairDivisionUnitTypeDTOList = repairDivisionUnitTypeService.listTypes()
                                                                                                     .stream()
                                                                                                     .map(RepairDivisionUnitTypeDTO::from)
                                                                                                     .collect(Collectors.toList());
        return ResponseEntity.ok(repairDivisionUnitTypeDTOList);
    }

    @PostMapping
    @ApiOperation(value = "Добавление типа РВО")
    public ResponseEntity<Object> addRepairStationType(@ApiParam(value = "Данные о типе РВО", required = true) @RequestBody RepairDivisionUnitTypeDTO repairDivisionUnitTypeDTO) {
        repairDivisionUnitTypeService.addType(repairDivisionUnitTypeDTO.getName(),
                                              repairDivisionUnitTypeDTO.getWorkingHoursMin(),
                                              repairDivisionUnitTypeDTO.getWorkingHoursMax());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{typeId}")
    @ApiOperation(value = "Обновление типа РВО")
    public ResponseEntity<Object> updateRepairStationType(@ApiParam(value = "Ключ типа РВО", required = true) @PathVariable Long typeId,
                                                          @ApiParam(value = "Данные о типе РВО", required = true) @RequestBody RepairDivisionUnitTypeDTO repairDivisionUnitTypeDTO) {
        repairDivisionUnitTypeService.updateType(typeId,
                                                 repairDivisionUnitTypeDTO.getName(),
                                                 repairDivisionUnitTypeDTO.getWorkingHoursMin(),
                                                 repairDivisionUnitTypeDTO.getWorkingHoursMax());
        return ResponseEntity.accepted().build();
    }

}
