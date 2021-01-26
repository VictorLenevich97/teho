package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.repairformation.RepairFormationTypeDTO;
import va.rit.teho.entity.repairformation.RepairFormationType;
import va.rit.teho.service.repairformation.RepairFormationTypeService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping(path = "formation/repair-formation/type", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Типы Ремонтных Формирований")
public class RepairFormationTypeController {

    private final RepairFormationTypeService repairFormationTypeService;

    public RepairFormationTypeController(RepairFormationTypeService repairFormationTypeService) {
        this.repairFormationTypeService = repairFormationTypeService;
    }

    @GetMapping
    @ApiOperation(value = "Получение списка типов Ремонтных Формирований")
    public ResponseEntity<List<RepairFormationTypeDTO>> listRepairFormationTypes() {
        List<RepairFormationTypeDTO> repairFormationTypeDTOList = repairFormationTypeService.listTypes()
                                                                                            .stream()
                                                                                            .map(RepairFormationTypeDTO::from)
                                                                                            .collect(Collectors.toList());
        return ResponseEntity.ok(repairFormationTypeDTOList);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавление типа Ремонтных Формирований")
    public ResponseEntity<RepairFormationTypeDTO> addRepairFormationType(@ApiParam(value = "Данные о типе Ремонтного Формирования", required = true) @Valid @RequestBody RepairFormationTypeDTO repairFormationTypeDTO) {
        RepairFormationType repairFormationType = repairFormationTypeService.addType(repairFormationTypeDTO.getName(),
                                                                                     repairFormationTypeDTO
                                                                                             .getRestorationType()
                                                                                             .getId(),
                                                                                     repairFormationTypeDTO.getWorkingHoursMin(),
                                                                                     repairFormationTypeDTO.getWorkingHoursMax());
        return ResponseEntity.status(HttpStatus.CREATED).body(RepairFormationTypeDTO.from(repairFormationType));
    }

    @PutMapping(path = "/{typeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление типа Ремонтных Формирований")
    public ResponseEntity<RepairFormationTypeDTO> updateRepairFormationType(@ApiParam(value = "Ключ типа Ремонтного Формирования", required = true) @PathVariable @Positive Long typeId,
                                                                            @ApiParam(value = "Данные о типе Ремонтного Формирования", required = true) @Valid @RequestBody RepairFormationTypeDTO repairFormationTypeDTO) {
        RepairFormationType repairFormationType = repairFormationTypeService.updateType(typeId,
                                                                                        repairFormationTypeDTO.getName(),
                                                                                        repairFormationTypeDTO.getWorkingHoursMin(),
                                                                                        repairFormationTypeDTO.getWorkingHoursMax());
        return ResponseEntity.accepted().body(RepairFormationTypeDTO.from(repairFormationType));
    }

    @DeleteMapping(path = "/{typeId}")
    @ApiOperation(value = "Удаление типа Ремонтных Формирований")
    public ResponseEntity<Object> deleteRepairFormationType(@ApiParam(value = "Ключ типа Ремонтного Формирования", required = true) @PathVariable @Positive Long typeId) {
        repairFormationTypeService.deleteType(typeId);
        return ResponseEntity.noContent().build();
    }

}
