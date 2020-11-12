package va.rit.teho.controller.repairstation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.repairstation.RepairStationTypeDTO;
import va.rit.teho.service.repairstation.RepairStationTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "repair-station/type", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Типы РВО")
public class RepairStationTypeController {

    private final RepairStationTypeService repairStationTypeService;

    public RepairStationTypeController(RepairStationTypeService repairStationTypeService) {
        this.repairStationTypeService = repairStationTypeService;
    }

    @GetMapping
    @ApiOperation(value = "Получение списка типов РВО")
    public ResponseEntity<List<RepairStationTypeDTO>> listRepairStationTypes() {
        List<RepairStationTypeDTO> repairStationTypeDTOList = repairStationTypeService.listTypes()
                                                                                      .stream()
                                                                                      .map(RepairStationTypeDTO::from)
                                                                                      .collect(Collectors.toList());
        return ResponseEntity.ok(repairStationTypeDTOList);
    }

    @PostMapping
    @ApiOperation(value = "Добавление типа РВО")
    public ResponseEntity<Object> addRepairStationType(@ApiParam(value = "Данные о типе РВО", required = true) @RequestBody RepairStationTypeDTO repairStationTypeDTO) {
        repairStationTypeService.addType(repairStationTypeDTO.getName(),
                                         repairStationTypeDTO.getWorkingHoursMin(),
                                         repairStationTypeDTO.getWorkingHoursMax());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{typeId}")
    @ApiOperation(value = "Обновление типа РВО")
    public ResponseEntity<Object> updateRepairStationType(@ApiParam(value = "Ключ типа РВО", required = true) @PathVariable Long typeId,
                                                          @ApiParam(value = "Данные о типе РВО", required = true) @RequestBody RepairStationTypeDTO repairStationTypeDTO) {
        repairStationTypeService.updateType(typeId,
                                            repairStationTypeDTO.getName(),
                                            repairStationTypeDTO.getWorkingHoursMin(),
                                            repairStationTypeDTO.getWorkingHoursMax());
        return ResponseEntity.accepted().build();
    }

}
