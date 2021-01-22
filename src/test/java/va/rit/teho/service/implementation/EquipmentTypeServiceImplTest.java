package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.util.Pair;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.equipment.EquipmentSubTypeRepository;
import va.rit.teho.repository.equipment.EquipmentTypeRepository;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.implementation.equipment.EquipmentTypeServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class EquipmentTypeServiceImplTest {

    private final EquipmentTypeRepository equipmentTypeRepository = Mockito.mock(EquipmentTypeRepository.class);
    private final EquipmentSubTypeRepository equipmentSubTypeRepository = Mockito.mock(EquipmentSubTypeRepository.class);

    private final EquipmentTypeService equipmentTypeService = new EquipmentTypeServiceImpl(equipmentTypeRepository,
                                                                                           equipmentSubTypeRepository);

    @Test
    public void testListTypes() {
        List<EquipmentType> equipmentTypes = Collections.singletonList(new EquipmentType("s", "f"));
        when(equipmentTypeRepository.findAll()).thenReturn(equipmentTypes);

        Assertions.assertEquals(equipmentTypes, equipmentTypeService.listTypes(Collections.emptyList()));
    }

    @Test
    public void testListTypesWithFilter() {
        List<EquipmentType> equipmentTypes = Collections.singletonList(new EquipmentType("s", "f"));
        Long equipmentId = 10L;
        when(equipmentTypeRepository.findAllById(Collections.singletonList(equipmentId))).thenReturn(equipmentTypes);

        Assertions.assertEquals(equipmentTypes, equipmentTypeService.listTypes(Collections.singletonList(equipmentId)));
    }

    @Test
    public void testAddType() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        EquipmentType addedEquipmentType = new EquipmentType(equipmentType.getShortName(), equipmentType.getFullName());
        addedEquipmentType.setId(15L);
        when(equipmentTypeRepository.save(equipmentType)).thenReturn(addedEquipmentType);

        Assertions.assertEquals(
                addedEquipmentType.getId(),
                equipmentTypeService.addType(equipmentType.getShortName(), equipmentType.getFullName()));
    }

    @Test
    public void testAddTypeAlreadyExists() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        when(equipmentTypeRepository.findByFullName(equipmentType.getFullName())).thenReturn(Optional.of(equipmentType));

        Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> equipmentTypeService.addType(equipmentType.getShortName(), equipmentType.getFullName()));
    }

    @Test
    public void testUpdateType() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        equipmentType.setId(15L);

        when(equipmentTypeRepository.findById(equipmentType.getId())).thenReturn(Optional.of(equipmentType));

        equipmentTypeService.updateType(equipmentType.getId(),
                                        equipmentType.getShortName(),
                                        equipmentType.getFullName());

        verify(equipmentTypeRepository).save(equipmentType);
    }


    @Test
    public void testUpdateTypeNotFound() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        equipmentType.setId(15L);

        when(equipmentTypeRepository.findById(equipmentType.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> equipmentTypeService.updateType(equipmentType.getId(),
                                                                                               equipmentType.getShortName(),
                                                                                               equipmentType.getFullName()));

        verify(equipmentTypeRepository).findById(equipmentType.getId());
        verifyNoMoreInteractions(equipmentTypeRepository);
    }

    @Test
    public void testAddSubType() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        equipmentType.setId(5L);
        EquipmentSubType equipmentSubType = new EquipmentSubType("s", "f", equipmentType);
        EquipmentSubType addedEquipmentSubType = new EquipmentSubType(equipmentSubType.getShortName(),
                                                                      equipmentSubType.getFullName(),
                                                                      equipmentType);
        addedEquipmentSubType.setId(10L);

        when(equipmentTypeRepository.findById(equipmentType.getId())).thenReturn(Optional.of(equipmentType));
        when(equipmentSubTypeRepository.save(equipmentSubType)).thenReturn(addedEquipmentSubType);

        Assertions.assertEquals(
                addedEquipmentSubType.getId(),
                equipmentTypeService.addSubType(equipmentType.getId(),
                                                equipmentSubType.getShortName(),
                                                equipmentSubType.getFullName()));
    }

    @Test
    public void testAddSubTypeIncorrectType() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        equipmentType.setId(5L);
        EquipmentSubType equipmentSubType = new EquipmentSubType("s", "f", equipmentType);
        EquipmentSubType addedEquipmentSubType = new EquipmentSubType(equipmentSubType.getShortName(),
                                                                      equipmentSubType.getFullName(),
                                                                      equipmentType);
        addedEquipmentSubType.setId(10L);

        when(equipmentTypeRepository.findById(equipmentType.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(IncorrectParamException.class, () ->
                equipmentTypeService.addSubType(equipmentType.getId(),
                                                equipmentSubType.getShortName(),
                                                equipmentSubType.getFullName()));
    }

    @Test
    public void testAddSubTypeAlreadyExists() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        equipmentType.setId(5L);
        EquipmentSubType equipmentSubType = new EquipmentSubType("s", "f", equipmentType);
        EquipmentSubType addedEquipmentSubType = new EquipmentSubType(equipmentSubType.getShortName(),
                                                                      equipmentSubType.getFullName(),
                                                                      equipmentType);
        addedEquipmentSubType.setId(10L);

        when(equipmentTypeRepository.findById(equipmentType.getId())).thenReturn(Optional.of(equipmentType));
        when(equipmentSubTypeRepository.findByFullName(equipmentSubType.getFullName())).thenReturn(Optional.of(
                equipmentSubType));

        Assertions.assertThrows(AlreadyExistsException.class, () ->
                equipmentTypeService.addSubType(equipmentType.getId(),
                                                equipmentSubType.getShortName(),
                                                equipmentSubType.getFullName()));
    }

    @Test
    public void testUpdateSubType() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        equipmentType.setId(5L);
        EquipmentSubType equipmentSubType = new EquipmentSubType("s", "f", equipmentType);
        EquipmentSubType addedEquipmentSubType = new EquipmentSubType(equipmentSubType.getShortName(),
                                                                      equipmentSubType.getFullName(),
                                                                      equipmentType);
        addedEquipmentSubType.setId(10L);

        when(equipmentTypeRepository.findById(equipmentType.getId())).thenReturn(Optional.of(equipmentType));
        when(equipmentSubTypeRepository.findById(addedEquipmentSubType.getId())).thenReturn(Optional.of(
                addedEquipmentSubType));
        when(equipmentSubTypeRepository.save(equipmentSubType)).thenReturn(addedEquipmentSubType);

        equipmentTypeService.updateSubType(addedEquipmentSubType.getId(),
                                           equipmentType.getId(),
                                           equipmentSubType.getShortName(),
                                           equipmentSubType.getFullName());

        verify(equipmentTypeRepository).findById(equipmentType.getId());
        verify(equipmentSubTypeRepository).findById(addedEquipmentSubType.getId());
    }

    @Test
    public void testListTypesWithSubTypes() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        EquipmentSubType equipmentSubType = new EquipmentSubType("s", "f", equipmentType);
        Map<EquipmentType, List<EquipmentSubType>> result = Collections.singletonMap(equipmentType,
                                                                                     Collections.singletonList(
                                                                                             equipmentSubType));

        when(equipmentSubTypeRepository.findByIds(Collections.emptyList(),
                                                  Collections.emptyList()))
                .thenReturn(Collections.singletonList(equipmentSubType));

        Assertions.assertEquals(result,
                                equipmentTypeService.listTypesWithSubTypes(Collections.emptyList(),
                                                                           Collections.emptyList()));
    }

    @Test
    public void testGetTypeWithSubTypes() {
        Long equipmentTypeId = 11L;
        EquipmentType equipmentType = new EquipmentType("short", "full");
        EquipmentSubType equipmentSubType = new EquipmentSubType("s", "f", equipmentType);
        Pair<EquipmentType, List<EquipmentSubType>> result = Pair.of(equipmentType,
                                                                     Collections.singletonList(equipmentSubType));

        when(equipmentTypeRepository.findById(equipmentTypeId)).thenReturn(Optional.of(equipmentType));
        when(equipmentSubTypeRepository.findByEquipmentTypeId(equipmentTypeId)).thenReturn(result.getSecond());

        Assertions.assertEquals(result, equipmentTypeService.getTypeWithSubTypes(equipmentTypeId));
    }
}
