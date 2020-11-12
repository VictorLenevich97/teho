package va.rit.teho.controller.equipment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.equipment.EquipmentSubTypeDTO;
import va.rit.teho.dto.equipment.EquipmentSubTypePerTypeDTO;
import va.rit.teho.dto.equipment.EquipmentTypeDTO;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.service.equipment.EquipmentTypeService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "equipment-type", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Типы и подтипы ВВСТ")
public class EquipmentTypeController {

    private final EquipmentTypeService equipmentTypeService;

    public EquipmentTypeController(EquipmentTypeService equipmentService) {
        this.equipmentTypeService = equipmentService;
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(value = "Получить типы ВВСТ")
    public ResponseEntity<List<EquipmentTypeDTO>> getEquipmentTypes(@ApiParam(value = "Ключи типов, по которым осуществляется фильтр") @RequestParam(value = "id", required = false) List<Long> typeIds) {
        List<Long> typeIdsFilter = typeIds == null ? Collections.emptyList() : typeIds;
        List<EquipmentTypeDTO> equipmentTypeDTOList = equipmentTypeService.listTypes(typeIdsFilter)
                                                                          .stream()
                                                                          .map(EquipmentTypeDTO::from)
                                                                          .collect(Collectors.toList());
        return ResponseEntity.ok(equipmentTypeDTOList);
    }

    @GetMapping("/subtype")
    @ResponseBody
    @ApiOperation(value = "Получить подтипы ВВСТ")
    public ResponseEntity<List<EquipmentSubTypePerTypeDTO>> getEquipmentSubTypes(
            @ApiParam(value = "Ключи подтипов, по которым осуществляется фильтр") @RequestParam(value = "id", required = false) List<Long> subTypeIds,
            @ApiParam(value = "Ключи типов, по которым осуществляется фильтр") @RequestParam(value = "typeId", required = false) List<Long> typeIds) {
        List<EquipmentSubTypePerTypeDTO> equipmentSubTypePerTypeDTOList =
                equipmentTypeService
                        .listTypesWithSubTypes(typeIds, subTypeIds)
                        .entrySet()
                        .stream()
                        .map(typeEntry -> EquipmentSubTypePerTypeDTO.from(typeEntry.getKey(), typeEntry.getValue()))
                        .collect(Collectors.toList());
        return ResponseEntity.ok(equipmentSubTypePerTypeDTOList);
    }

    @GetMapping("/{typeId}")
    @ResponseBody
    @ApiOperation(value = "Получить данные о типе ВВСТ (со списком подтипов)")
    public ResponseEntity<EquipmentSubTypePerTypeDTO> getEquipmentTypeById(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable Long typeId) {
        Pair<EquipmentType, List<EquipmentSubType>> typeWithSubTypes = equipmentTypeService.getTypeWithSubTypes(typeId);
        return ResponseEntity.ok(EquipmentSubTypePerTypeDTO.from(typeWithSubTypes.getFirst(),
                                                                 typeWithSubTypes.getSecond()));
    }

    @PostMapping
    @ApiOperation(value = "Добавить тип ВВСТ")
    public ResponseEntity<Object> addEquipmentType(@ApiParam("Данные о типе ВВСТ") @RequestBody EquipmentTypeDTO equipmentTypeDTO) {
        equipmentTypeService.addType(equipmentTypeDTO.getShortName(), equipmentTypeDTO.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{typeId}")
    @ApiOperation(value = "Обновить тип ВВСТ")
    public ResponseEntity<Object> updateEquipmentType(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable Long typeId,
                                                      @ApiParam(value = "Данные о типе ВВСТ", required = true) @RequestBody EquipmentTypeDTO equipmentTypeDTO) {
        equipmentTypeService.updateType(typeId, equipmentTypeDTO.getShortName(), equipmentTypeDTO.getFullName());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/{typeId}/subtype")
    @ApiOperation(value = "Добавить подтип ВВСТ")
    public ResponseEntity<Object> addEquipmentSubType(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable Long typeId,
                                                      @ApiParam(value = "Данные о подтипе ВВСТ", required = true) @RequestBody EquipmentSubTypeDTO equipmentSubTypeDTO) {
        equipmentTypeService.addSubType(typeId, equipmentSubTypeDTO.getShortName(), equipmentSubTypeDTO.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{typeId}/subtype/{subTypeId}")
    @ApiOperation(value = "Обновить подтип ВВСТ")
    public ResponseEntity<Object> updateEquipmentSubType(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable Long typeId,
                                                         @ApiParam(value = "Ключ подтипа ВВСТ", required = true) @PathVariable Long subTypeId,
                                                         @ApiParam(value = "Данные о подтипе ВВСТ", required = true) @RequestBody EquipmentSubTypeDTO equipmentSubTypeDTO) {
        equipmentTypeService.updateSubType(subTypeId,
                                           typeId,
                                           equipmentSubTypeDTO.getShortName(),
                                           equipmentSubTypeDTO.getFullName());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
