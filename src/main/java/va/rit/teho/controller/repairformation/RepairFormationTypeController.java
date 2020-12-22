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
import va.rit.teho.service.repairformation.RepairFormationTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "formation/repair-formation/type", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Типы Ремонтных Формирований")
public class RepairFormationTypeController {

    private final RepairFormationTypeService repairFormationTypeService;

    public RepairFormationTypeController(RepairFormationTypeService repairFormationTypeService) {
        this.repairFormationTypeService = repairFormationTypeService;
    }

    @GetMapping
    @ApiOperation(value = "Получение списка типов Ремонтных Формирований")
    public ResponseEntity<List<RepairFormationTypeDTO>> listRepairStationTypes() {
        List<RepairFormationTypeDTO> repairFormationTypeDTOList = repairFormationTypeService.listTypes()
                                                                                            .stream()
                                                                                            .map(RepairFormationTypeDTO::from)
                                                                                            .collect(Collectors.toList());
        return ResponseEntity.ok(repairFormationTypeDTOList);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавление типа Ремонтных Формирований")
    public ResponseEntity<Object> addRepairStationType(@ApiParam(value = "Данные о типе Ремонтного Формирования", required = true) @RequestBody RepairFormationTypeDTO repairFormationTypeDTO) {
        repairFormationTypeService.addType(repairFormationTypeDTO.getName(),
                                           repairFormationTypeDTO.getWorkingHoursMin(),
                                           repairFormationTypeDTO.getWorkingHoursMax());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(path = "/{typeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление типа Ремонтных Формирований")
    public ResponseEntity<Object> updateRepairStationType(@ApiParam(value = "Ключ типа Ремонтного Формирования", required = true) @PathVariable Long typeId,
                                                          @ApiParam(value = "Данные о типе Ремонтного Формирования", required = true) @RequestBody RepairFormationTypeDTO repairFormationTypeDTO) {
        repairFormationTypeService.updateType(typeId,
                                              repairFormationTypeDTO.getName(),
                                              repairFormationTypeDTO.getWorkingHoursMin(),
                                              repairFormationTypeDTO.getWorkingHoursMax());
        return ResponseEntity.accepted().build();
    }

}
