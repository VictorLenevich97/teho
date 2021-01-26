package va.rit.teho.controller.equipment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.equipment.EquipmentSubTypeDTO;
import va.rit.teho.dto.equipment.EquipmentSubTypePerTypeDTO;
import va.rit.teho.dto.equipment.EquipmentTypeDTO;
import va.rit.teho.entity.equipment.EquipmentSubType;
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
    public ResponseEntity<List<EquipmentSubTypeDTO>> getEquipmentSubTypes(
            @ApiParam(value = "Ключи типов, по которым осуществляется фильтр") @RequestParam(value = "typeId", required = false) List<Long> typeIds) {
        List<EquipmentSubTypeDTO> equipmentSubTypePerTypeDTOList =
                equipmentTypeService
                        .listSubTypes(typeIds)
                        .stream()
                        .map(EquipmentSubTypeDTO::from)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(equipmentSubTypePerTypeDTOList);
    }

    @GetMapping("/subtype-grouped")
    @ResponseBody
    @ApiOperation(value = "Получить подтипы ВВСТ, сгруппированные с типами")
    public ResponseEntity<List<EquipmentSubTypePerTypeDTO>> getEquipmentSubTypesGrouped(
            @ApiParam(value = "Ключи подтипов, по которым осуществляется фильтр") @RequestParam(value = "id", required = false) List<Long> subTypeIds,
            @ApiParam(value = "Ключи типов, по которым осуществляется фильтр") @RequestParam(value = "typeId", required = false) List<Long> typeIds) {
        List<EquipmentSubTypePerTypeDTO> equipmentSubTypePerTypeDTOList =
                equipmentTypeService
                        .listTypesWithSubTypes(typeIds, subTypeIds)
                        .entrySet()
                        .stream()
                        .flatMap(typeEntry -> EquipmentSubTypePerTypeDTO.from(typeEntry.getKey(), typeEntry.getValue()))
                        .collect(Collectors.toList());
        return ResponseEntity.ok(equipmentSubTypePerTypeDTOList);
    }

    @GetMapping("/{typeId}")
    @ResponseBody
    @ApiOperation(value = "Получить данные о типе ВВСТ (со списком подтипов)")
    public ResponseEntity<EquipmentSubTypePerTypeDTO> getEquipmentTypeById(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable @Positive Long typeId) {
        Pair<EquipmentType, List<EquipmentSubType>> typeWithSubTypes = equipmentTypeService.getTypeWithSubTypes(typeId);
        return ResponseEntity.ok(EquipmentSubTypePerTypeDTO
                                         .from(typeWithSubTypes.getFirst(),
                                               typeWithSubTypes.getSecond())
                                         .collect(Collectors.toList())
                                         .get(0));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавить тип ВВСТ")
    public ResponseEntity<EquipmentTypeDTO> addEquipmentType(@ApiParam("Данные о типе ВВСТ") @Valid @RequestBody EquipmentTypeDTO equipmentTypeDTO) {
        EquipmentType equipmentType = equipmentTypeService.addType(equipmentTypeDTO.getShortName(),
                                                                   equipmentTypeDTO.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).body(EquipmentTypeDTO.from(equipmentType));
    }

    @PutMapping(path = "/{typeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить тип ВВСТ")
    public ResponseEntity<Object> updateEquipmentType(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable @Positive Long typeId,
                                                      @ApiParam(value = "Данные о типе ВВСТ", required = true) @Valid @RequestBody EquipmentTypeDTO equipmentTypeDTO) {
        EquipmentType equipmentType = equipmentTypeService.updateType(typeId,
                                                                      equipmentTypeDTO.getShortName(),
                                                                      equipmentTypeDTO.getFullName());
        return ResponseEntity.accepted().body(EquipmentTypeDTO.from(equipmentType));
    }

    @DeleteMapping(path = "/{typeId}")
    @ApiOperation(value = "Удалить тип ВВСТ.")
    public ResponseEntity<Object> deleteEquipmentType(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable @Positive Long typeId) {
        equipmentTypeService.deleteType(typeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{typeId}/subtype", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавить подтип ВВСТ")
    public ResponseEntity<EquipmentSubTypeDTO> addEquipmentSubType(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable @Positive Long typeId,
                                                                   @ApiParam(value = "Данные о подтипе ВВСТ", required = true) @Valid @RequestBody EquipmentSubTypeDTO equipmentSubTypeDTO) {
        EquipmentSubType equipmentSubType = equipmentTypeService.addSubType(typeId,
                                                                            equipmentSubTypeDTO.getShortName(),
                                                                            equipmentSubTypeDTO.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).body(EquipmentSubTypeDTO.from(equipmentSubType));
    }

    @PutMapping(path = "/{typeId}/subtype/{subTypeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить подтип ВВСТ")
    public ResponseEntity<EquipmentSubTypeDTO> updateEquipmentSubType(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable @Positive Long typeId,
                                                                      @ApiParam(value = "Ключ подтипа ВВСТ", required = true) @PathVariable @Positive Long subTypeId,
                                                                      @ApiParam(value = "Данные о подтипе ВВСТ", required = true) @Valid @RequestBody EquipmentSubTypeDTO equipmentSubTypeDTO) {
        EquipmentSubType equipmentSubType = equipmentTypeService.updateSubType(subTypeId,
                                                                               typeId,
                                                                               equipmentSubTypeDTO.getShortName(),
                                                                               equipmentSubTypeDTO.getFullName());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(EquipmentSubTypeDTO.from(equipmentSubType));
    }

    @DeleteMapping(path = "/{typeId}/subtype/{subTypeId}")
    @ApiOperation(value = "Удалить подтип ВВСТ.")
    public ResponseEntity<Object> deleteEquipmentSubType(@ApiParam(value = "Ключ типа ВВСТ", required = true) @PathVariable @Positive Long typeId,
                                                         @ApiParam(value = "Ключ подтипа ВВСТ", required = true) @PathVariable @Positive Long subTypeId) {
        equipmentTypeService.deleteSubType(subTypeId);
        return ResponseEntity.noContent().build();
    }
}
