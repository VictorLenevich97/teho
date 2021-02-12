package va.rit.teho.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.common.RepairTypeDTO;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.service.common.RepairTypeService;

import javax.validation.constraints.Positive;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping(path = "repair-type", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Типы ремонта")
public class RepairTypeController {

    private final RepairTypeService repairTypeService;

    public RepairTypeController(RepairTypeService repairTypeService) {
        this.repairTypeService = repairTypeService;
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(value = "Получить список типов ремонта")
    public ResponseEntity<List<RepairTypeDTO>> listRepairTypes(
            @ApiParam(value = "Фильтр по индикатору, определяющему используется ли тип ремонта в расчетах. В случае, когда параметр не указан, возвращаются все типы ремонта.",
                    example = "true") @RequestParam(required = false) Boolean calculatable,
            @ApiParam(value = "Фильтр по индикатору, определяющему ремонтнопригодность.",
                    example = "true") @RequestParam(required = false) Boolean repairable) {
        List<RepairType> types =
                Optional.ofNullable(calculatable).map(repairTypeService::list).orElse(repairTypeService.list());

        return ResponseEntity.ok(types
                                         .stream()
                                         .filter(rt -> repairable == null || (rt.isRepairable() == repairable))
                                         .map(RepairTypeDTO::from)
                                         .sorted(Comparator.comparing(RepairTypeDTO::getId))
                                         .collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Обновление типа ремонта (смена флага, определяющего использование в отчетах)")
    public ResponseEntity<RepairTypeDTO> updateRepairType(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(RepairTypeDTO.from(repairTypeService.switchCalculatableFlag(id)));
    }
}
