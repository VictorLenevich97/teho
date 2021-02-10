package va.rit.teho.controller.equipment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.equipment.EquipmentTypeDTO;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.service.equipment.EquipmentTypeService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping(path = "equipment-type", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Типы и подтипы ВВСТ")
@Transactional
public class EquipmentTypeController {

    private final EquipmentTypeService equipmentTypeService;

    public EquipmentTypeController(EquipmentTypeService equipmentService) {
        this.equipmentTypeService = equipmentService;
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(value = "Получить типы ВВСТ")
    public ResponseEntity<List<EquipmentTypeDTO>> getEquipmentTypes(
            @RequestParam(required = false, defaultValue = "false") boolean grouped,
            @ApiParam(value = "Ключи типов, по которым осуществляется фильтр") @RequestParam(value = "id", required = false) List<Long> typeIds) {
        List<Long> typeIdsFilter = typeIds == null ? Collections.emptyList() : typeIds;
        List<EquipmentTypeDTO> equipmentTypeDTOList;
        if (grouped) {
            equipmentTypeDTOList = equipmentTypeService
                    .listHighestLevelTypes(typeIdsFilter)
                    .stream()
                    .map(EquipmentTypeDTO::fromEntityIncludeSubtypes)
                    .collect(Collectors.toList());
        } else {
            equipmentTypeDTOList = equipmentTypeService.listTypes(typeIds)
                                                       .stream()
                                                       .map(EquipmentTypeDTO::fromEntity)
                                                       .collect(Collectors.toList());
        }
        return ResponseEntity.ok(equipmentTypeDTOList);
    }

    @GetMapping("/{typeId}")
    @ResponseBody
    @ApiOperation(value = "Получить данные о типе ВВСТ (со списком подтипов)")
    public ResponseEntity<EquipmentTypeDTO> getEquipmentTypeById(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable @Positive Long typeId) {
        EquipmentType type = equipmentTypeService.get(typeId);
        return ResponseEntity.ok(EquipmentTypeDTO.fromEntityIncludeSubtypes(type));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавить тип ВВСТ")
    public ResponseEntity<EquipmentTypeDTO> addEquipmentType(@ApiParam("Данные о типе ВВСТ") @Valid @RequestBody EquipmentTypeDTO equipmentTypeDTO) {
        EquipmentType equipmentType;
        if (equipmentTypeDTO.getParentTypeId() == null) {
            equipmentType = equipmentTypeService.addType(equipmentTypeDTO.getShortName(),
                                                         equipmentTypeDTO.getFullName());
        } else {
            equipmentType = equipmentTypeService.addType(equipmentTypeDTO.getParentTypeId(),
                                                         equipmentTypeDTO.getShortName(),
                                                         equipmentTypeDTO.getFullName());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(EquipmentTypeDTO.fromEntity(equipmentType));
    }

    @PutMapping(path = "/{typeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить тип ВВСТ")
    public ResponseEntity<Object> updateEquipmentType(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable @Positive Long typeId,
                                                      @ApiParam(value = "Данные о типе ВВСТ", required = true) @Valid @RequestBody EquipmentTypeDTO equipmentTypeDTO) {
        EquipmentType equipmentType;
        if (equipmentTypeDTO.getParentTypeId() == null) {
            equipmentType = equipmentTypeService.updateType(typeId,
                                                            equipmentTypeDTO.getShortName(),
                                                            equipmentTypeDTO.getFullName());
        } else {
            equipmentType = equipmentTypeService.updateType(typeId,
                                                            equipmentTypeDTO.getParentTypeId(),
                                                            equipmentTypeDTO.getShortName(),
                                                            equipmentTypeDTO.getFullName());
        }
        return ResponseEntity.accepted().body(EquipmentTypeDTO.fromEntity(equipmentType));
    }

    @DeleteMapping(path = "/{typeId}")
    @ApiOperation(value = "Удалить тип ВВСТ.")
    public ResponseEntity<Object> deleteEquipmentType(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable @Positive Long typeId) {
        equipmentTypeService.deleteType(typeId);
        return ResponseEntity.noContent().build();
    }
}
